// import HomePage from '@/components/PresidentElection';
import Head from 'next/head';
import { Plus } from 'lucide-react';
import { useState } from 'react';

import CreateVoteDialog from '@/components/CreateVote';

export default function Home() {
	const [isCreateVoteOpen, setIsCreateVoteOpen] = useState(false);

	return (
		<>
			<Head>
				<title>
					{/* 제 21대 대통령 선거 투표_“21대 대통령 선거에 누구를 뽑으셨나요?” */}
					모두의 투표
				</title>
			</Head>

			{/* <HomePage /> */}
			<div className="flex flex-col items-center justify-center h-screen">
				<p className="text-2xl font-bold">모두의 투표가 준비중 입니다.</p>
				<p className="text-sm text-muted-foreground">개발자는 열일 중...</p>
			</div>
			<div>
				<button
					type="button"
					className="fixed bottom-6 right-6 z-50 w-14 h-14 rounded-full bg-blue-900 text-white shadow-lg flex items-center justify-center hover:bg-blue-800 transition-colors"
					aria-label="투표 생성 버튼"
					onClick={() => setIsCreateVoteOpen(true)}
				>
					<Plus className="w-8 h-8 font-bold" />
				</button>
			</div>
			<CreateVoteDialog
				isOpen={isCreateVoteOpen}
				setIsOpen={setIsCreateVoteOpen}
			/>
		</>
	);
}
