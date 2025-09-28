// import HomePage from '@/components/PresidentElection';
import Head from 'next/head';

import { useState } from 'react';

export default function Home() {
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
		</>
	);
}
