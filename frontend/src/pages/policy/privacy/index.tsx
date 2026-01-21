import Head from 'next/head';

import { useRouter } from 'next/router';
import Footer from '@/components/layout/Footer';

import PrivacyContents from '@/components/Policy/PrivacyContents';

export default function PrivacyPage() {
	const router = useRouter();

	return (
		<>
			<Head>
				<title>개인정보 수집·이용에 대한 동의 | KEP</title>
			</Head>

			<div className="min-h-screen bg-slate-50 px-4 py-8 md:py-12">
				<PrivacyContents />
			</div>
		</>
	);
}
