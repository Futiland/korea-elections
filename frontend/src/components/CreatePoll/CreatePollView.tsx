import {
	Controller,
	type Control,
	type FieldArrayWithId,
	type FieldErrors,
	type UseFormHandleSubmit,
	type UseFormRegister,
	type UseFormRegisterReturn,
} from 'react-hook-form';

import { Dialog, DialogContent } from '@/components/CustomDialog';
import { Button } from '@/components/ui/button';
import {
	Field,
	FieldDescription,
	FieldGroup,
	FieldLabel,
	FieldSet,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from '@/components/ui/select';
import { Switch } from '@/components/ui/switch';
import { formatDateTimeLocal, parseDateTimeLocal } from '@/lib/date';
import type { QuestionType } from '@/lib/types/poll';

import type { CreatePollFormValues } from './useCreatePollPresenter';

type FormPresenter = {
	handleFormSubmit: ReturnType<UseFormHandleSubmit<CreatePollFormValues>>;
	handleCancel: () => void;
	register: UseFormRegister<CreatePollFormValues>;
	registerOption: (index: number) => UseFormRegisterReturn;
	control: Control<CreatePollFormValues>;
	errors: FieldErrors<CreatePollFormValues>;
	isSubmitting: boolean;
	fields: FieldArrayWithId<CreatePollFormValues, 'options', 'id'>[];
	appendOption: () => void;
	removeOption: (index: number) => void;
	responseType: QuestionType;
	titleLength: number;
	descriptionLength: number;
	handleOptionType: (value: QuestionType) => void;
};

export type CreatePollViewProps = {
	dialog: {
		isOpen: boolean;
		onOpenChange: (open: boolean) => void;
	};
	form: FormPresenter;
};

export function CreatePollView({ dialog, form }: CreatePollViewProps) {
	const {
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
		titleLength,
		descriptionLength,
		handleOptionType,
	} = form;

	return (
		<Dialog open={dialog.isOpen} onOpenChange={dialog.onOpenChange}>
			<DialogContent
				closeOnOverlayClick={false}
				className="max-h-[calc(100vh-60px)] w-[calc(100%-40px)] overflow-y-scroll bg-slate-100 px-5 py-6"
			>
				<form onSubmit={handleFormSubmit}>
					<FieldGroup>
						<FieldSet>
							<div>
								<h2 className="text-xl font-bold">모두의 투표 만들기</h2>
								<span className="text-sm text-slate-500">
									필수 항목을 입력해 주세요.
								</span>
							</div>

							<FieldGroup className="gap-2">
								<Field>
									<FieldLabel className="text-md font-bold">
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
										<span className="text-slate-500">{titleLength}/50</span>
									</div>
								</Field>

								<Field>
									<FieldLabel className="text-md font-bold">
										투표 내용
									</FieldLabel>
									<textarea
										placeholder="투표의 상세 내용을 작성해주세요."
										maxLength={300}
										className="min-h-24 w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
										{...register('description')}
									/>
									<div className="flex justify-between text-xs">
										<span className="text-red-600">
											{errors.description?.message?.toString()}
										</span>
										<span className="text-slate-500">
											{descriptionLength}/300
										</span>
									</div>
								</Field>

								<Field>
									<FieldLabel className="text-md font-bold">
										종료일<span className="text-xs text-blue-500">(필수)</span>
										<span className="text-xs font-normal text-slate-500">
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
													field.onChange(parseDateTimeLocal(e.target.value))
												}
											/>
										)}
									/>
									<FieldDescription className="text-xs text-red-600">
										{errors.endAt?.message?.toString()}
									</FieldDescription>
								</Field>

								<Field>
									<FieldLabel className="text-md font-bold">
										투표 폼 타입
										<span className="text-xs text-blue-500">(필수)</span>
									</FieldLabel>
									<Controller
										control={control}
										name="responseType"
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
									<FieldDescription className="text-xs text-red-600">
										{errors.responseType?.message?.toString()}
									</FieldDescription>
								</Field>

								{responseType !== 'SCORE' && (
									<Field>
										<FieldLabel className="text-md font-bold">
											옵션 설정
											<span className="text-xs text-blue-500">(필수)</span>
											<span className="text-xs font-normal text-slate-500">
												옵션은 최대 10개까지 추가할 수 있습니다.
											</span>
										</FieldLabel>
										<FieldGroup className="gap-2">
											{fields.map((fieldItem, idx) => (
												<Field key={fieldItem.id}>
													<div className="flex items-center gap-2">
														<div className="flex-1">
															<Input
																type="text"
																placeholder={`옵션 ${idx + 1}`}
																maxLength={50}
																className="text-sm"
																{...registerOption(idx)}
															/>
															{errors.options &&
																Array.isArray(errors.options) &&
																errors.options[idx]?.optionText && (
																	<div className="mt-1 text-xs text-red-600">
																		{
																			errors.options[idx]?.optionText
																				?.message as string
																		}
																	</div>
																)}
														</div>
														<Button
															type="button"
															variant="ghost"
															className="text-xs text-slate-500"
															onClick={() => removeOption(idx)}
														>
															제거
														</Button>
													</div>
												</Field>
											))}
										</FieldGroup>
										<FieldDescription className="text-xs text-red-600">
											{(errors.options?.root?.message as string) ||
												(errors.options?.message as string) ||
												''}
										</FieldDescription>

										<div className="mb-2 flex items-center justify-between">
											<Button
												type="button"
												variant="outline"
												className="h-8 px-2"
												onClick={appendOption}
												disabled={fields.length >= 10}
											>
												옵션 추가
											</Button>
										</div>
									</Field>
								)}
							</FieldGroup>

							<FieldGroup className="gap-2">
								<Field orientation="horizontal">
									<FieldLabel className="text-md font-bold">
										재투표 가능 여부
									</FieldLabel>
									<Controller
										control={control}
										name="isRevotable"
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
									className="flex-1 h-10 bg-blue-100 text-blue-900 hover:bg-blue-200"
									onClick={handleCancel}
								>
									취소
								</Button>
								<Button
									type="submit"
									disabled={isSubmitting}
									className="flex-1 h-10 bg-blue-800 text-white hover:bg-blue-700"
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

export default CreatePollView;
