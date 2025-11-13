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

import { Button } from '@/components/ui/button';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';
import { QuestionType } from '@/lib/types/poll';
import type { CreatePollViewProps } from './CreatePollView';

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
		questionType: z.enum(['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'SCORE']),
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
		allowRetriableResponses: z.boolean(),
	})
	.refine(
		(v) => v.questionType === 'SCORE' || (v.options && v.options.length >= 2),
		{
			message: '단일/다중 선택은 최소 2개 이상의 옵션이 필요합니다',
			path: ['options'],
		}
	)
	.refine(
		(v) => {
			if (v.questionType === 'SCORE') return true;
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
	questionType: 'SINGLE_CHOICE',
	options: [{ optionText: '' }, { optionText: '' }],
	allowRetriableResponses: false,
};

export function useCreatePollPresenter({
	isOpen,
	setIsOpen,
}: CreatePollDialogProps) {
	const { showDialog, hideDialog } = useAlertDialog();

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

	const { fields, append, remove } = useFieldArray({
		control,
		name: 'options',
	});

	const questionType = watch('questionType');
	const titleVal = watch('title') ?? '';
	const descriptionVal = watch('description') ?? '';
	const optionsVal = watch('options') ?? [];

	const validateOptions = useCallback(() => {
		if (questionType !== 'SCORE') {
			trigger('options');
		}
	}, [questionType, trigger]);

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
			console.log('onSubmit values:', values);

			try {
				// submit placeholder
				toast.success('유효성 검사를 통과했습니다. 제출 로직을 연결하세요.');
				setIsOpen(false);
			} catch (e) {
				toast.error('제출 중 오류가 발생했습니다.');
			}
		},
		[setIsOpen]
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
				questionType,
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
			questionType,
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
