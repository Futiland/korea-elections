// 차트 컴포넌트들의 props 데이터 샘플

// 1. ScoreChart (점수제) 데이터 샘플 - 1점부터 10점까지 완전한 데이터
export const scoreChartSampleData = {
	data: [
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
	],
	totalResponses: 200,
	height: 300,
};

// 2. MultipleChoiceChart (다중선택) 데이터 샘플
export const multipleChoiceChartSampleData = {
	data: [
		{ option: '옵션 A', count: 45, percentage: 22.5 },
		{ option: '옵션 B', count: 38, percentage: 19.0 },
		{ option: '옵션 C', count: 32, percentage: 16.0 },
		{ option: '옵션 D', count: 28, percentage: 14.0 },
		{ option: '옵션 E', count: 25, percentage: 12.5 },
		{ option: '옵션 F', count: 20, percentage: 10.0 },
		{ option: '옵션 G', count: 12, percentage: 6.0 },
	],
	totalResponses: 200,
	height: 300,
};

// 3. SingleChoiceChart (단일선택) 데이터 샘플
export const singleChoiceChartSampleData = {
	data: [
		{ option: '후보자 A', count: 85, percentage: 42.5 },
		{ option: '후보자 B', count: 65, percentage: 32.5 },
		{ option: '후보자 C', count: 35, percentage: 17.5 },
		{ option: '후보자 D', count: 15, percentage: 7.5 },
	],
	totalResponses: 200,
	height: 300,
};

// 실제 API 응답에서 변환하는 예시 함수들
export const convertScoreData = (apiResponse: any) => {
	// API 응답을 ScoreChart 형식으로 변환
	const totalResponses = apiResponse.totalCount || 0;
	const scoreData = apiResponse.scores || [];

	return {
		data: scoreData.map((item: any) => ({
			score: item.score,
			count: item.count,
			percentage: totalResponses > 0 ? (item.count / totalResponses) * 100 : 0,
		})),
		totalResponses,
		height: 300,
	};
};

export const convertMultipleChoiceData = (apiResponse: any) => {
	// API 응답을 MultipleChoiceChart 형식으로 변환
	const totalResponses = apiResponse.totalCount || 0;
	const options = apiResponse.options || [];

	return {
		data: options.map((item: any) => ({
			option: item.name,
			count: item.count,
			percentage: totalResponses > 0 ? (item.count / totalResponses) * 100 : 0,
		})),
		totalResponses,
		height: 300,
	};
};

export const convertSingleChoiceData = (apiResponse: any) => {
	// API 응답을 SingleChoiceChart 형식으로 변환
	const totalResponses = apiResponse.totalCount || 0;
	const options = apiResponse.options || [];

	return {
		data: options.map((item: any) => ({
			option: item.name,
			count: item.count,
			percentage: totalResponses > 0 ? (item.count / totalResponses) * 100 : 0,
		})),
		totalResponses,
		height: 300,
	};
};

// 사용 예시
export const chartUsageExamples = {
	// ScoreChart 사용 예시
	scoreChart: `
import ScoreChart from '@/components/Charts/ScoreChart';
import { scoreChartSampleData } from '@/components/Charts/ChartDataSamples';

<ScoreChart
  data={scoreChartSampleData.data}
  totalResponses={scoreChartSampleData.totalResponses}
  height={scoreChartSampleData.height}
/>
  `,

	// MultipleChoiceChart 사용 예시
	multipleChoiceChart: `
import MultipleChoiceChart from '@/components/Charts/MultipleChoiceChart';
import { multipleChoiceChartSampleData } from '@/components/Charts/ChartDataSamples';

<MultipleChoiceChart
  data={multipleChoiceChartSampleData.data}
  totalResponses={multipleChoiceChartSampleData.totalResponses}
  height={multipleChoiceChartSampleData.height}
/>
  `,

	// SingleChoiceChart 사용 예시
	singleChoiceChart: `
import SingleChoiceChart from '@/components/Charts/SingleChoiceChart';
import { singleChoiceChartSampleData } from '@/components/Charts/ChartDataSamples';

<SingleChoiceChart
  data={singleChoiceChartSampleData.data}
  totalResponses={singleChoiceChartSampleData.totalResponses}
  height={singleChoiceChartSampleData.height}
/>
  `,
};
