import { useCallback, useMemo } from 'react';
import {
	useForm,
	useFieldArray,
	type UseFormRegisterReturn,
	type Resolver,
} from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { toast } from 'sonner';
import { useMutation, useQueryClient } from '@tanstack/react-query';

import { Button } from '@/components/ui/button';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';
import { createPoll } from '@/lib/api/poll';
import {
	CreatePollData,
	CreatePollResponse,
	QuestionType,
} from '@/lib/types/poll';
import type { CreatePollViewProps } from './CreatePollView';
import router from 'next/router';

export type CreatePollDialogProps = {
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
};

const createPollSchema = z
	.object({
		title: z
			.string()
			.min(1, '투표 제목은 필수입니다')
			.max(50, '최대 50자까지 가능합니다'),
		description: z
			.string()
			.max(300, '최대 300자까지 가능합니다')
			.optional()
			.or(z.literal('')),
		endAt: z.coerce
			.date()
			.refine((d) => !isNaN(d.getTime()), '종료일을 선택해주세요')
			.refine((d) => d.getTime() > Date.now(), '종료일은 현재 이후여야 합니다'),
		responseType: z.enum(['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'SCORE']),
		options: z
			.array(
				z.object({
					optionText: z
						.string()
						.min(1, '1자 이상 입력해주세요')
						.refine((val) => val.trim().length >= 1, '옵션을 입력해주세요')
						.max(50, '옵션은 최대 50자'),
				})
			)
			.max(10, '옵션은 최대 10개'),
		isRevotable: z.boolean(),
	})
	.refine(
		(v) => v.responseType === 'SCORE' || (v.options && v.options.length >= 2),
		{
			message: '단일/다중 선택은 최소 2개 이상의 옵션이 필요합니다',
			path: ['options'],
		}
	)
	.refine(
		(v) => {
			if (v.responseType === 'SCORE') return true;
			if (!v.options) return true;
			const validOptions = v.options.filter(
				(opt) => opt && opt.optionText && opt.optionText.trim().length >= 1
			);
			return validOptions.length >= 2;
		},
		{
			message: '최소 2개 이상의 유효한 옵션이 필요합니다 (각 옵션은 1자 이상)',
			path: ['options'],
		}
	);

export type CreatePollFormValues = z.infer<typeof createPollSchema>;

const defaultValues: CreatePollFormValues = {
	title: '',
	description: '',
	endAt: new Date(Date.now() + 60 * 60 * 1000),
	responseType: 'SINGLE_CHOICE',
	options: [{ optionText: '' }, { optionText: '' }],
	isRevotable: false,
};

const toCreatePollData = (values: CreatePollFormValues): CreatePollData => {
	const { title, description, responseType, endAt, isRevotable } = values;
	const options =
		responseType === 'SCORE'
			? []
			: values.options.map((option) => ({
					optionText: option.optionText.trim(),
			  }));

	// Date 객체를 UTC ISO 문자열로 변환하여 서버에 전송
	// 사용자가 선택한 로컬 시간을 UTC로 변환하여 서버에 전송
	// 예: 2025-11-18T00:41:00.000Z (UTC)
	const endAtISO: string = endAt.toISOString();

	return {
		title,
		description: (description ?? '').trim(),
		responseType,
		endAt: endAtISO, // UTC ISO 문자열로 전송
		isRevotable,
		options,
	};
};

export function useCreatePollPresenter({
	isOpen,
	setIsOpen,
}: CreatePollDialogProps) {
	const { showDialog, hideDialog } = useAlertDialog();
	const queryClient = useQueryClient();

	const {
		control,
		register,
		handleSubmit,
		watch,
		reset,
		setValue,
		trigger,
		formState: { errors, isSubmitting },
	} = useForm<CreatePollFormValues>({
		resolver: zodResolver(createPollSchema) as Resolver<CreatePollFormValues>,
		defaultValues,
		mode: 'onChange',
	});

	const createPollMutation = useMutation({
		mutationFn: (payload: CreatePollData) => createPoll(payload),
		onSuccess: (data: CreatePollResponse) => {
			// everyone-poll 페이지의 리스트만 리패치
			queryClient.invalidateQueries({ queryKey: ['publicPolls'] });
			toast.success('투표 생성이 완료되었습니다.');
			hideDialog();
			setIsOpen(false);
			reset(defaultValues);
			router.push(`/everyone-poll/${data.data.id}`);
		},
		onError: () => {
			toast.error('투표 생성에 실패했습니다.');
			hideDialog();
		},
	});

	const { fields, append, remove } = useFieldArray({
		control,
		name: 'options',
	});

	const responseType = watch('responseType');
	const titleVal = watch('title') ?? '';
	const descriptionVal = watch('description') ?? '';
	const optionsVal = watch('options') ?? [];

	const validateOptions = useCallback(() => {
		if (responseType !== 'SCORE') {
			trigger('options');
		}
	}, [responseType, trigger]);

	const registerOption = useCallback(
		(index: number): UseFormRegisterReturn =>
			register(`options.${index}.optionText` as const, {
				onChange: () => validateOptions(),
			}),
		[register, validateOptions]
	);

	const appendOption = useCallback(() => {
		append({ optionText: '' });
		setTimeout(() => validateOptions(), 0);
	}, [append, validateOptions]);

	const removeOption = useCallback(
		(index: number) => {
			remove(index);
			setTimeout(() => validateOptions(), 0);
		},
		[remove, validateOptions]
	);

	const handleOptionType = useCallback(
		(value: QuestionType) => {
			if (value === 'SCORE') {
				setValue('options', []);
			} else if (!optionsVal || optionsVal.length === 0) {
				setValue('options', [{ optionText: '' }]);
			}
			setTimeout(() => validateOptions(), 0);
		},
		[optionsVal, setValue, validateOptions]
	);

	const handleCancel = useCallback(() => {
		showDialog({
			message: '투표 생성을 취소하시겠습니까?',
			actions: (
				<div className="flex w-full gap-2">
					<Button
						className="flex-1 h-10 bg-slate-200 text-slate-900 hover:bg-slate-200"
						onClick={() => hideDialog()}
					>
						계속 작성
					</Button>
					<Button
						className="flex-1 h-10 bg-red-600 text-white hover:bg-red-500"
						onClick={() => {
							reset(defaultValues);
							hideDialog();
							setIsOpen(false);
						}}
					>
						투표 생성 취소
					</Button>
				</div>
			),
		});
	}, [hideDialog, reset, setIsOpen, showDialog]);

	const onSubmit = useCallback(
		async (values: CreatePollFormValues) => {
			const payload = toCreatePollData(values);
			// 디버깅: 사용자가 선택한 원본 시간과 서버에 보낼 시간 모두 표시
			// console.log('사용자가 선택한 시간 (로컬):', values.endAt);
			// console.log('서버에 보낼 시간 (UTC ISO):', payload.endAt);
			// console.log('전체 payload:', payload);

			showDialog({
				message: '모투의 투표를 생성합니다.',
				discription: '생성된 투표는 수정이 불가능합니다. 생성 하시겠습니까?',
				actions: (
					<div className="flex w-full gap-2">
						<Button
							className="flex-1 h-10 bg-slate-200 text-slate-900 hover:bg-slate-200"
							onClick={() => hideDialog()}
						>
							취소
						</Button>
						<Button
							className="flex-1 h-10 bg-blue-800 text-white hover:bg-blue-700"
							disabled={createPollMutation.isPending}
							onClick={() => createPollMutation.mutate(payload)}
						>
							{createPollMutation.isPending ? '생성 중...' : '생성'}
						</Button>
					</div>
				),
			});
		},
		[createPollMutation, hideDialog, showDialog]
	);

	const handleFormSubmit = useMemo(
		() => handleSubmit(onSubmit),
		[handleSubmit, onSubmit]
	);

	const formPresenter = useMemo(
		() =>
			({
				handleFormSubmit,
				handleCancel,
				register,
				registerOption,
				control,
				errors,
				isSubmitting,
				fields,
				appendOption,
				removeOption,
				responseType,
				titleLength: titleVal.length,
				descriptionLength: descriptionVal.length,
				handleOptionType,
			} satisfies CreatePollViewProps['form']),
		[
			handleFormSubmit,
			handleCancel,
			register,
			registerOption,
			control,
			errors,
			isSubmitting,
			fields,
			appendOption,
			removeOption,
			responseType,
			titleVal.length,
			descriptionVal.length,
			handleOptionType,
		]
	);

	return {
		dialog: {
			isOpen,
			onOpenChange: setIsOpen,
		},
		form: formPresenter,
	} satisfies CreatePollViewProps;
}
