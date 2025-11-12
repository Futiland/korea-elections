import SingleChoiceChart from '../Charts/SingleChoiceChart';
import MultipleChoiceChart from '../Charts/MultipleChoiceChart';
import ScoreChart from '../Charts/ScoreChart';

export default function PollCardResults() {
	return (
		<div className="">
			<SingleChoiceChart
				data={[
					{ option: '후보자 A', count: 100, percentage: 50 },
					{ option: '후보자 B', count: 30, percentage: 15 },
					{ option: '후보자 C', count: 70, percentage: 35 },
				]}
				totalResponses={100}
			/>

			<MultipleChoiceChart
				data={[
					{ option: '옵션 A', count: 45, percentage: 22.5 },
					{ option: '옵션 B', count: 38, percentage: 19.0 },
				]}
				totalResponses={100}
			/>

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
		</div>
	);
}
