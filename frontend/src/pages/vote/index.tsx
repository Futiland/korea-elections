import { GetServerSideProps } from 'next';

type VotePageProps = {
	questions: string[];
};

export default function VotePage({ questions }: VotePageProps) {
	return (
		<div className="p-8">
			<h1 className="text-2xl font-bold mb-4">투표하기</h1>
			<ul className="space-y-2">
				{questions.map((q, idx) => (
					<li key={idx} className="border p-4 rounded">
						{q}
					</li>
				))}
			</ul>
		</div>
	);
}

export const getServerSideProps: GetServerSideProps = async () => {
	// 서버에서 데이터 불러오기 (예: DB나 API)
	const questions = ['후보 A vs 후보 B', '정책 1 vs 정책 2']; // 예시

	return {
		props: {
			questions,
		},
	};
};
