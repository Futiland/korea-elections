import { useMemo, useEffect } from 'react';
import { useForm, Controller, useFieldArray } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';

import { Dialog, DialogContent, DialogFooter } from '@/components/CustomDialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import {
	Field,
	FieldDescription,
	FieldGroup,
	FieldLabel,
	FieldLegend,
	FieldSeparator,
	FieldSet,
} from '@/components/ui/field';
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from '@/components/ui/select';
import { Switch } from '@/components/ui/switch';
import { useAuthToken } from '@/hooks/useAuthToken';
import { toast } from 'sonner';
import router from 'next/router';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';
import { QuestionType } from '@/lib/types/poll';
import { formatDateTimeLocal } from '@/lib/date';

type CreatePollDialogProps = {
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
};

export default function CreatePollDialog({
	isOpen,
	setIsOpen,
}: CreatePollDialogProps) {
	const { isLoggedIn, isReady } = useAuthToken();
	const { showDialog, hideDialog } = useAlertDialog();

	// Zod schema
	const schema = useMemo(
		() =>
			z
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
						.refine(
							(d) => d.getTime() > Date.now(),
							'종료일은 현재 이후여야 합니다'
						),
					questionType: z.enum(['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'SCORE']),
					options: z
						.array(
							z.object({
								optionText: z
									.string()
									.min(1, '1자 이상 입력해주세요')
									.refine(
										(val) => val.trim().length >= 1,
										'옵션을 입력해주세요'
									)
									.max(50, '옵션은 최대 50자'),
							})
						)
						.max(10, '옵션은 최대 10개')
						.default([]),
					allowRetriableResponses: z.boolean().default(false),
				})
				.refine(
					(v) =>
						v.questionType === 'SCORE' || (v.options && v.options.length >= 2),
					{
						message: '단일/다중 선택은 최소 2개 이상의 옵션이 필요합니다',
						path: ['options'],
					}
				)
				.refine(
					(v) => {
						if (v.questionType === 'SCORE') return true;
						if (!v.options) return true;
						// 빈 옵션을 필터링한 후 검사
						const validOptions = v.options.filter(
							(opt) =>
								opt && opt.optionText && opt.optionText.trim().length >= 1
						);
						// 유효한 옵션이 2개 이상이어야 함
						return validOptions.length >= 2;
					},
					{
						message:
							'최소 2개 이상의 유효한 옵션이 필요합니다 (각 옵션은 1자 이상)',
						path: ['options'],
					}
				),
		[]
	);

	type FormValues = z.infer<typeof schema>;

	const getDefaultValues = () => ({
		title: '',
		description: '',
		endAt: new Date(Date.now() + 60 * 60 * 1000), // 기본 1시간 후
		questionType: 'SINGLE_CHOICE' as QuestionType,
		options: [{ optionText: '' }],
		allowRetriableResponses: false,
	});

	const {
		control,
		register,
		handleSubmit,
		watch,
		reset,
		setValue,
		getValues,
		trigger,
		formState: { errors, isSubmitting },
	} = useForm<any>({
		resolver: zodResolver(schema),
		defaultValues: getDefaultValues(),
		mode: 'onChange',
	});

	const { fields, append, remove } = useFieldArray({
		control,
		name: 'options' as const,
	});

	const questionType = watch('questionType');
	const titleVal = watch('title') ?? '';
	const descriptionVal = watch('description') ?? '';
	const optionsVal = watch('options') ?? [];

	const handleOptionType = (value: QuestionType) => {
		if (value === 'SCORE') {
			setValue('options', []);
		} else {
			// SINGLE_CHOICE 또는 MULTIPLE_CHOICE로 변경할 때
			setValue('options', ['']);
		}
	};

	const onSubmit = async (values: FormValues) => {
		console.log('onSubmit values:', values);
		console.log('getValues():', getValues());

		// try {
		// 	// submit placeholder
		// 	toast.success('유효성 검사를 통과했습니다. 제출 로직을 연결하세요.');
		// 	setIsOpen(false);
		// } catch (e) {
		// 	toast.error('제출 중 오류가 발생했습니다.');
		// }
	};

	const onError = (errors: any) => {
		console.log('유효성 검사 실패:', errors);
		console.log('현재 form 값:', getValues());
	};

	const handleCancel = () => {
		showDialog({
			message: '투표 생성을 취소하시겠습니까?',
			actions: (
				<div className="flex gap-2 w-full">
					<Button
						className="flex-1 bg-slate-200 text-slate-900 hover:bg-slate-200 h-10"
						onClick={() => hideDialog()}
					>
						계속 작성
					</Button>
					<Button
						className="flex-1 bg-red-600 hover:bg-red-500 text-white h-10"
						onClick={() => {
							reset(getDefaultValues());
							hideDialog();
							setIsOpen(false);
						}}
					>
						투표 생성 취소
					</Button>
				</div>
			),
		});
	};

	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent
				closeOnOverlayClick={false}
				className="w-[calc(100%-40px)] bg-slate-100 px-5 py-6 max-h-[calc(100vh-60px)] overflow-y-scroll"
			>
				<form onSubmit={handleSubmit(onSubmit, onError)}>
					<FieldGroup>
						<FieldSet>
							<div className="">
								<h2 className="font-bold text-md text-xl">
									모두의 투표 만들기
								</h2>
								<span className="text-sm text-slate-500">
									필수 항목을 입력해 주세요.
								</span>
							</div>

							<FieldGroup className="gap-2">
								{/* 제목 */}
								<Field>
									<FieldLabel className="font-bold text-md">
										투표 제목
										<span className="text-xs text-blue-500">(필수)</span>
									</FieldLabel>
									<Input
										type="text"
										placeholder="투표의 제목을 작성해주세요."
										maxLength={50}
										className="text-sm"
										{...register('title')}
									/>
									<div className="flex justify-between text-xs">
										<span className="text-red-600">
											{errors.title?.message?.toString()}
										</span>
										<span className="text-slate-500">{titleVal.length}/50</span>
									</div>
								</Field>

								{/* 내용 */}
								<Field>
									<FieldLabel className="font-bold text-md">
										투표 내용
										{/* <span className="text-xs text-blue-500">(선택)</span> */}
									</FieldLabel>
									<textarea
										placeholder="투표의 상세 내용을 작성해주세요."
										maxLength={300}
										className="w-full min-h-24 rounded-md border border-slate-300 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
										{...register('description')}
									/>
									<div className="flex justify-between text-xs">
										<span className="text-red-600">
											{errors.description?.message?.toString()}
										</span>
										<span className="text-slate-500">
											{descriptionVal.length}/300
										</span>
									</div>
								</Field>

								{/* 종료일 */}
								<Field>
									<FieldLabel className="font-bold text-md">
										종료일
										<span className="text-xs text-blue-500">(필수)</span>
										<span className="text-xs text-slate-500 font-normal">
											시작일은 생성 즉시 시작됩니다.
										</span>
									</FieldLabel>
									<Controller
										control={control}
										name="endAt"
										render={({ field }) => (
											<Input
												type="datetime-local"
												value={formatDateTimeLocal(field.value)}
												className="text-sm"
												onChange={(e) =>
													field.onChange(new Date(e.target.value))
												}
											/>
										)}
									/>
									<FieldDescription className="text-red-600 text-xs">
										{errors.endAt?.message?.toString()}
									</FieldDescription>
								</Field>

								{/* 폼 타입 */}
								<Field>
									<FieldLabel className="font-bold text-md">
										투표 폼 타입
										<span className="text-xs text-blue-500">(필수)</span>
									</FieldLabel>
									<Controller
										control={control}
										name="questionType"
										render={({ field }) => (
											<Select
												value={field.value}
												onValueChange={(value) => {
													field.onChange(value);
													handleOptionType(value as QuestionType);
												}}
											>
												<SelectTrigger className="bg-white">
													<SelectValue placeholder="폼 타입을 선택해 주세요." />
												</SelectTrigger>
												<SelectContent>
													<SelectItem value="SCORE">점수제</SelectItem>
													<SelectItem value="SINGLE_CHOICE">
														단일 선택
													</SelectItem>
													<SelectItem value="MULTIPLE_CHOICE">
														다중 선택
													</SelectItem>
												</SelectContent>
											</Select>
										)}
									/>
									<FieldDescription className="text-red-600 text-xs">
										{errors.questionType?.message?.toString()}
									</FieldDescription>
								</Field>

								{/* 옵션 (SCORE 제외) */}
								{questionType !== 'SCORE' && (
									<Field>
										<FieldLabel className="font-bold text-md">
											옵션 설정
											<span className="text-xs text-blue-500">(필수)</span>
											<span className="text-xs text-slate-500 font-normal">
												옵션은 최대 10개까지 추가할 수 있습니다.
											</span>
										</FieldLabel>
										<FieldGroup className="gap-2">
											{fields.map((f, idx) => (
												<Field key={f.id}>
													{/* <FieldLabel>옵션 {idx + 1}</FieldLabel> */}
													<div className="flex items-center gap-2">
														<div className="flex-1">
															<Input
																type="text"
																placeholder={`옵션 ${idx + 1}`}
																maxLength={50}
																className="text-sm"
																{...register(
																	`options.${idx}.optionText` as const,
																	{
																		onChange: () => {
																			// 옵션 값 변경 시 유효성 검사 재실행
																			if (questionType !== 'SCORE') {
																				trigger('options');
																			}
																		},
																	}
																)}
															/>
															{errors.options &&
																Array.isArray(errors.options) &&
																errors.options[idx]?.optionText && (
																	<div className="text-xs text-red-600 mt-1">
																		{
																			errors.options[idx]?.optionText
																				?.message as string
																		}
																	</div>
																)}
															{/* <div className="flex justify-end text-xs mt-1">
																<span className="text-slate-500">
																	{optionsVal[idx]?.optionText?.length ?? 0}/50
																</span>
															</div> */}
														</div>
														<Button
															type="button"
															variant="ghost"
															className="text-xs text-slate-500"
															onClick={() => {
																remove(idx);
																// 옵션 제거 후 유효성 검사 재실행
																if (questionType !== 'SCORE') {
																	setTimeout(() => trigger('options'), 0);
																}
															}}
														>
															제거
														</Button>
													</div>
												</Field>
											))}
										</FieldGroup>
										<FieldDescription className="text-red-600 text-xs">
											{(errors.options?.root?.message as string) ||
												(errors.options?.message as string) ||
												''}
										</FieldDescription>

										<div className="mb-2 flex items-center justify-between">
											<Button
												type="button"
												variant="outline"
												className="h-8 px-2"
												onClick={() => {
													append({ optionText: '' });
													// 옵션 추가 후 유효성 검사 재실행
													if (questionType !== 'SCORE') {
														setTimeout(() => trigger('options'), 0);
													}
												}}
												disabled={fields.length >= 10}
											>
												옵션 추가
											</Button>
										</div>
									</Field>
								)}
							</FieldGroup>

							{/* 재투표 가능 여부 */}
							<FieldGroup className="gap-2">
								<Field orientation="horizontal">
									<FieldLabel className="font-bold text-md">
										재투표 가능 여부
									</FieldLabel>
									<Controller
										control={control}
										name="allowRetriableResponses"
										render={({ field }) => (
											<Switch
												className="data-[state=checked]:bg-blue-500"
												checked={field.value}
												onCheckedChange={(checked) => field.onChange(!!checked)}
											/>
										)}
									/>
								</Field>
								<FieldDescription className="text-slate-500">
									재투표 가능이 활성화 되면 이미 투표한 사용자도 다시 투표할 수
									있습니다.
								</FieldDescription>
							</FieldGroup>
						</FieldSet>

						<Field>
							<div className="flex gap-3">
								<Button
									type="button"
									className="flex-1 bg-blue-100 text-blue-900 hover:bg-blue-200 h-10"
									onClick={handleCancel}
								>
									취소
								</Button>
								<Button
									type="submit"
									disabled={isSubmitting}
									className="flex-1 bg-blue-900 text-white hover:bg-blue-800 h-10"
								>
									{isSubmitting ? '제출 중...' : '투표 생성'}
								</Button>
							</div>
						</Field>
					</FieldGroup>
				</form>
			</DialogContent>
		</Dialog>
	);
}
