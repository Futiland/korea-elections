import { GetServerSideProps } from 'next';

import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

type resultPageProps = {
	questions: string[];
};

const ages = [
	{
		age: '20대',
		id: 20,
	},
	{
		age: '30대',
		id: 30,
	},
	{
		age: '40대',
		id: 40,
	},
	{
		age: '50대',
		id: 50,
	},
	{
		age: '60대~',
		id: 60,
	},
];

export default function resultPage({ questions }: resultPageProps) {
	return (
		<div className="min-h-screen py-7 px-4 w-full max-w-lg mx-auto">
			<div className="flex items-center justify-between mb-4">
				<h1 className="text-xl font-bold">투표 결과</h1>
				<p className="tex-sm text-slate-500">
					최신 업데이트 : 2025.12.23 09:00
				</p>
			</div>

			{/* <Tabs defaultValue="account" className="">
				<TabsList className="grid w-full grid-cols-2">
					<TabsTrigger value="20">20</TabsTrigger>
					<TabsTrigger value="30">30</TabsTrigger>
				</TabsList>
				<TabsContent value="20"></TabsContent>
			</Tabs> */}
			<Tabs defaultValue="40대" className="w-full">
				<TabsList className="w-full grid grid-cols-5 p-1 rounded-lg border border-slate-300 h-10 bg-white">
					{ages.map((item) => (
						<TabsTrigger
							key={item.id}
							value={item.age}
							className="text-sm data-[state=active]:bg-blue-100 data-[state=active]:shadow-sm"
						>
							{item.age}
						</TabsTrigger>
					))}
				</TabsList>
			</Tabs>
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
