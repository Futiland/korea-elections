import SingleChoiceChart from '../Charts/SingleChoiceChart';
import MultipleChoiceChart from '../Charts/MultipleChoiceChart';
import ScoreChart from '../Charts/ScoreChart';
import { PublicPollResultResponse, QuestionType } from '@/lib/types/poll';
import { useQuery } from '@tanstack/react-query';
import { getPublicPollResult } from '@/lib/api/poll';
import { Loader2 } from 'lucide-react';

type PollCardResultsProps = {
	canShowResults: boolean;
	pollId: number;
	responsType: QuestionType;
};

export default function PollCardResults({
	canShowResults,
	pollId,
	responsType,
}: PollCardResultsProps) {
	const { data, isFetching, isError } = useQuery<
		PublicPollResultResponse | { data: PublicPollResultResponse }
	>({
		queryKey: ['publicPollResult', pollId],
		queryFn: () => getPublicPollResult(pollId),
		enabled: canShowResults,
	});

	const resultData =
		data && 'data' in data
			? (data as { data: PublicPollResultResponse }).data
			: (data as PublicPollResultResponse | undefined);

	if (isFetching) {
		return (
			<div className="h-70 flex items-center justify-center">
				<Loader2 className="animate-spin mx-auto my-6 h-6 w-6" />
			</div>
		);
	}

	if (isError) {
		return (
			<div className="h-70 flex items-center justify-center text-sm text-red-600">
				결과를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.
			</div>
		);
	}

	return (
		<div className="">
			{responsType === 'SINGLE_CHOICE' && (
				<SingleChoiceChart
					data={resultData?.optionResults || []}
					totalResponses={resultData?.totalResponseCount || 0}
				/>
			)}

			{responsType === 'MULTIPLE_CHOICE' && (
				<MultipleChoiceChart
					data={resultData?.optionResults || []}
					totalResponses={resultData?.totalResponseCount || 0}
				/>
			)}

			{responsType === 'SCORE' && (
				<ScoreChart
					data={[
						{ score: 1, count: 3, percentage: 1.5 },
						{ score: 2, count: 7, percentage: 3.5 },
						{ score: 3, count: 12, percentage: 6.0 },
						{ score: 4, count: 18, percentage: 9.0 },
						{ score: 5, count: 25, percentage: 12.5 },
						{ score: 6, count: 32, percentage: 16.0 },
						{ score: 7, count: 38, percentage: 19.0 },
						{ score: 8, count: 35, percentage: 17.5 },
						{ score: 9, count: 22, percentage: 11.0 },
						{ score: 10, count: 8, percentage: 4.0 },
					]}
					totalResponses={100}
				/>
			)}
		</div>
	);
}
