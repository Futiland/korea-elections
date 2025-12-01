import { useCallback, useState } from 'react';
import { Card, CardHeader, CardTitle } from '../ui/card';
import StatusBadge from '../StatusBadge';
import PollCardOptions from './PollCardOptions';
import { Loader2, Share2, Users } from 'lucide-react';
import { addCommas } from '@/lib/utils';
import PollCardResults from './PollCardResults';
import {
	PublicPollData,
	PollStatus,
	QuestionType,
	PublicPollSubmitResponse,
} from '@/lib/types/poll';
import { formatDateTimeLocal, getRemainingTimeLabel } from '@/lib/date';
import { OptionData } from '@/lib/types/poll';
import { submitPublicPoll } from '@/lib/api/poll';
import {
	useIsFetching,
	useMutation,
	useQueryClient,
} from '@tanstack/react-query';
import { toast } from 'sonner';
import { useRequireLogin } from '@/hooks/useRequireLogin';

interface PollCardProps {
	pollData: PublicPollData;
}

export default function PollCard({ pollData }: PollCardProps) {
	const [showResults, setShowResults] = useState<boolean>(false);
	const [selectedOptionValue, setSelectedOptionValue] = useState<
		number[] | number
	>([]);
	const { ensureLoggedIn } = useRequireLogin();

	const queryClient = useQueryClient();
	const isRefreshingPolls = useIsFetching({ queryKey: ['publicPolls'] }) > 0;

	const isExpired = pollData?.status === 'EXPIRED';
	const hasParticipants =
		!!pollData?.responseCount && pollData.responseCount > 0;

	const participationMessage = isExpired
		? hasParticipants
			? `총 ${addCommas(pollData.responseCount)}명이 참여했습니다.`
			: '아쉽게도 참여자가 없었습니다. 😢'
		: hasParticipants
		? `지금까지 ${addCommas(pollData.responseCount)}명이 참여했어요!`
		: '첫 번째 참여자가 되어 주세요!';

	const remainingTimeLabel = pollData?.endAt
		? getRemainingTimeLabel(pollData.endAt)
		: null;

	const submitPublicPollMutation = useMutation({
		mutationFn: (payload: {
			pollId: number;
			optionId: number[] | number;
			responseType: QuestionType;
		}) =>
			submitPublicPoll(payload.pollId, payload.optionId, payload.responseType),
		onSuccess: () => {
			toast.success('투표가 완료되었습니다.');
			queryClient.invalidateQueries({ queryKey: ['publicPolls'] });
			// setShowResults(true);
		},
		onError: (data: PublicPollSubmitResponse) => {
			toast.error(data.message);
		},
	});

	const onSharePoll = useCallback(() => {
		// TODO: 투표 공유 링크 생성
		navigator.clipboard.writeText(window.location.href);
	}, []);

	const handlePollResultView = useCallback(
		(showResults: boolean) =>
			ensureLoggedIn({
				onSuccess: () => setShowResults(showResults),
				description: '투표 결과를 확인하려면 로그인이 필요합니다.',
			}),
		[ensureLoggedIn]
	);

	const handlePollSubmit = useCallback(() => {
		if (
			Array.isArray(selectedOptionValue) &&
			selectedOptionValue.length === 0
		) {
			toast.error('투표 옵션을 선택해주세요.');
			return;
		}

		const submitPoll = () =>
			submitPublicPollMutation.mutate({
				pollId: pollData?.id,
				optionId: selectedOptionValue,
				responseType: pollData?.responseType,
			});

		ensureLoggedIn({
			onSuccess: submitPoll,
			description: '투표 참여는 로그인 후 가능합니다.',
		});
	}, [
		ensureLoggedIn,
		pollData?.id,
		pollData?.responseType,
		selectedOptionValue,
		submitPublicPollMutation,
	]);

	return (
		<Card className="w-full transition-colors">
			<div className="px-6 py-4">
				{/* 헤더 영역 - 참여자 수, 상태값, 제목, 공유 버튼 */}
				{/* 참여 독력 메세지 */}

				<div className="flex justify-between items-center mb-3">
					<div className="inline-flex items-center gap-2 rounded-full bg-fuchsia-50 px-3 py-1 text-sm font-medium text-fuchsia-600">
						<Users className="w-4 h-4 text-fuchsia-600" />
						<span>
							{participationMessage}
							{remainingTimeLabel ? ` · ${remainingTimeLabel}` : ''}
						</span>
					</div>
					<button
						className="bg-slate-200 hover:bg-slate-100 py-2 px-2 rounded-full font-semibold"
						type="button"
						title="공유하기"
						onClick={onSharePoll}
					>
						<Share2 className="w-4 h-4 text-slate-700" />
					</button>
				</div>

				<div className="flex items-center justify-between mb-2">
					<div className="flex items-center gap-3">
						<StatusBadge status={pollData?.status ?? 'IN_PROGRESS'} />

						<span className="text-xs text-slate-500">
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
						<p className="text-xs text-slate-500">
							{pollData.creatorInfo.name}
						</p>
					)}
				</div>

				{/* 투표 제목 */}
				<CardHeader className="px-0 py-0">
					<CardTitle className="text-lg">{pollData?.title}</CardTitle>
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
						onChange={setSelectedOptionValue}
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
							onClick={() => handlePollResultView(false)}
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
											onClick={handlePollSubmit}
											disabled={submitPublicPollMutation.isPending}
										>
											{submitPublicPollMutation.isPending ||
											isRefreshingPolls ? (
												<Loader2 className="w-6 h-6 animate-spin text-center" />
											) : (
												'투표하기'
											)}
										</button>
									)}

									{/* 투표 완료 */}
									{pollData?.isVoted && !pollData?.isRevotable && (
										<button
											className="w-full bg-green-600 hover:bg-green-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors"
											type="button"
											onClick={() => handlePollResultView(true)}
										>
											결과보기
										</button>
									)}

									{/* 재투표 가능 */}
									{pollData?.isVoted && pollData?.isRevotable && (
										<>
											<button
												className="flex-1 bg-blue-800 hover:bg-blue-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors shadow-sm flex items-center justify-center"
												type="button"
												onClick={handlePollSubmit}
												disabled={submitPublicPollMutation.isPending}
											>
												{submitPublicPollMutation.isPending ||
												isRefreshingPolls ? (
													<Loader2 className="w-6 h-6 animate-spin text-center" />
												) : (
													'다시 투표하기'
												)}
											</button>
											<button
												className="flex-1 bg-green-600 hover:bg-green-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors"
												type="button"
												onClick={() => handlePollResultView(true)}
											>
												결과보기
											</button>
										</>
									)}
								</>
							)}

							{/* 종료된 투표 */}
							{pollData?.status === 'EXPIRED' && pollData?.isVoted && (
								<button
									className="w-full bg-green-600 hover:bg-green-700 text-white py-3 px-4 rounded-lg font-semibold transition-colors"
									type="button"
									onClick={() => handlePollResultView(true)}
								>
									결과보기
								</button>
							)}
						</>
					)}
				</div>
			</div>
		</Card>
	);
}
