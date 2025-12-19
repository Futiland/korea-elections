import { Card, CardHeader, CardTitle } from '@/components/ui/card';
import StatusBadge from '@/components/StatusBadge';
import PollCardOptions from '@/components/PollCard/PollCardOptions';
import PollCardResults from '@/components/PollCard/PollCardResults';
import { PollParticipationBadge } from '@/components/PollCard/PollParticipationBadge';
import { Button } from '@/components/ui/button';
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogHeader,
	DialogTitle,
} from '@/components/ui/dialog';
import { CheckCircle2, Loader2, Share2 } from 'lucide-react';
import { formatDateTimeLocal } from '@/lib/date';

import type { PollCardViewProps } from './usePollCardPresenter';

export default function PollCardView({
	pollData,
	showResults,
	isRefreshingPolls,
	participationMessage,
	remainingTimeLabel,
	isShareDialogOpen,
	setIsShareDialogOpen,
	shareUrl,
	selectedOptionValue,
	onChangeSelectedOption,
	onClickShare,
	onCopyShareUrl,
	onShareKakao,
	onClickShowResults,
	onSubmitPoll,
	isSubmittingPoll,
	isLoggedIn,
}: PollCardViewProps) {
	return (
		<>
			<Card className="w-full transition-colors">
				<div className="px-6 py-4">
					{/* 헤더 영역 - 참여자 수, 상태값, 제목, 공유 버튼 */}
					{/* 참여 독력 메세지 */}

					<div className="mb-3 flex items-center justify-between">
						<PollParticipationBadge
							participationMessage={participationMessage}
							remainingTimeLabel={remainingTimeLabel}
						/>
						<button
							className="bg-slate-100 hover:bg-slate-200 py-2 px-2 rounded-full font-semibold"
							type="button"
							title="공유하기"
							onClick={onClickShare}
						>
							<Share2 className="w-4 h-4 text-slate-700" />
						</button>
					</div>

					<div className="flex items-center justify-between mb-2">
						<div className="flex items-center gap-1 md:gap-3">
							<StatusBadge status={pollData?.status ?? 'IN_PROGRESS'} />

							<span className="text-xs text-slate-700">
								{pollData?.startAt && pollData?.endAt
									? `${formatDateTimeLocal(
											pollData.startAt,
											'yyyy-MM-dd HH:mm'
									  )} ~ ${formatDateTimeLocal(
											pollData.endAt,
											'yyyy-MM-dd HH:mm'
									  )}`
									: ''}
							</span>
						</div>
						{pollData?.creatorInfo && (
							<p className="text-xs text-slate-500 min-w-[34px]">
								{pollData.creatorInfo.name}
							</p>
						)}
					</div>

					{/* 투표 제목 */}
					<CardHeader className="px-0 py-0">
						<CardTitle className="text-lg">
							{pollData?.title}
							<span className="text-xs text-slate-400 ml-2">
								{pollData?.responseType === 'MULTIPLE_CHOICE' &&
									'(복수 선택 가능)'}
							</span>
						</CardTitle>
					</CardHeader>

					{/* 상세 내용 */}
					<div className="mb-6 text-sm text-slate-600 leading-relaxed">
						{pollData?.description}
					</div>

					{/* 선택 옵션 */}
					{!showResults && pollData && (
						<PollCardOptions
							responseType={pollData.responseType}
							options={pollData.options}
							onChange={onChangeSelectedOption}
							isVoted={
								(pollData.isVoted && !pollData.isRevotable) ||
								pollData?.status === 'EXPIRED'
							}
						/>
					)}

					{/* 결과 차트 */}
					{showResults && (
						<PollCardResults
							canShowResults={showResults}
							pollId={pollData.id}
							responsType={pollData.responseType}
						/>
					)}

					{/* 버튼 영역 */}
					<div className="flex gap-3">
						{showResults ? (
							<button
								className="w-full bg-blue-800 hover:bg-blue-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors shadow-sm"
								type="button"
								onClick={() => onClickShowResults(false)}
							>
								투표 보기
							</button>
						) : (
							<>
								{/* 진행 중 상태 */}
								{pollData?.status === 'IN_PROGRESS' && (
									<>
										{/* 투표 미참여 */}
										{!pollData?.isVoted && (
											<button
												className="flex-1 bg-blue-800 hover:bg-blue-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors shadow-sm flex items-center justify-center"
												type="button"
												onClick={onSubmitPoll}
												disabled={isSubmittingPoll}
											>
												{isSubmittingPoll || isRefreshingPolls ? (
													<Loader2 className="w-6 h-6 animate-spin text-center" />
												) : (
													'투표하기'
												)}
											</button>
										)}

										{/* 투표 완료 */}
										{pollData?.isVoted && !pollData?.isRevotable && (
											<button
												className="w-full bg-green-600 hover:bg-green-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors flex items-center justify-center gap-2"
												type="button"
												onClick={() => onClickShowResults(true)}
											>
												<CheckCircle2 className="w-5 h-5" />
												<span>투표 완료 · 결과 보기</span>
											</button>
										)}

										{/* 재투표 가능 */}
										{pollData?.isVoted && pollData?.isRevotable && (
											<>
												<button
													className="flex-1 bg-blue-800 hover:bg-blue-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors shadow-sm flex items-center justify-center"
													type="button"
													onClick={onSubmitPoll}
													disabled={isSubmittingPoll}
												>
													{isSubmittingPoll || isRefreshingPolls ? (
														<Loader2 className="w-6 h-6 animate-spin text-center" />
													) : (
														'다시 투표하기'
													)}
												</button>
												<button
													className="flex-1 bg-green-600 hover:bg-green-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors flex items-center justify-center gap-2"
													type="button"
													onClick={() => onClickShowResults(true)}
												>
													<span>결과 보기</span>
												</button>
											</>
										)}
									</>
								)}

								{/* 종료된 투표 - 로그인 사용자만 결과 보기 버튼 표시 */}
								{pollData?.status === 'EXPIRED' && pollData?.isVoted && (
									<button
										className="w-full bg-green-600 hover:bg-green-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors flex items-center justify-center gap-2"
										type="button"
										onClick={() => onClickShowResults(true)}
									>
										<CheckCircle2 className="w-5 h-5" />
										<span>투표 완료 · 결과 보기</span>
									</button>
								)}

								{/* 종료된 투표 - 비로그인 상태 버튼 표시 */}
								{pollData?.status === 'EXPIRED' && !isLoggedIn && (
									<button
										className="w-full bg-green-600 hover:bg-green-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors flex items-center justify-center gap-2"
										type="button"
										onClick={() => onClickShowResults(true)}
									>
										<span>로그인 후 결과 보기</span>
									</button>
								)}
							</>
						)}
					</div>
				</div>
			</Card>

			<Dialog
				open={isShareDialogOpen}
				onOpenChange={(open) => setIsShareDialogOpen(open)}
			>
				<DialogContent className="sm:max-w-md">
					<DialogHeader>
						<DialogTitle>투표 공유하기</DialogTitle>
						<DialogDescription>
							친구들과 링크를 공유하거나 카카오톡으로 바로 전달해보세요.
						</DialogDescription>
					</DialogHeader>
					<div className="space-y-4">
						{/* <div className="border rounded-lg px-3 py-2 bg-slate-50 text-xs sm:text-sm text-slate-700 break-all">
							{shareUrl || '링크를 불러오는 중입니다...'}
						</div> */}
						<div className="flex flex-col sm:flex-row gap-2">
							<Button
								className="flex-1"
								variant="secondary"
								type="button"
								onClick={onCopyShareUrl}
								disabled={!shareUrl}
							>
								URL 복사하기
							</Button>
							<Button
								className="flex-1 bg-[#FEE500] hover:bg-[#F9D000] text-slate-900"
								type="button"
								onClick={onShareKakao}
								disabled={!shareUrl}
							>
								카카오톡 공유
							</Button>
						</div>
					</div>
				</DialogContent>
			</Dialog>
		</>
	);
}
