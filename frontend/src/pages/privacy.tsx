import Head from 'next/head';
import { Button } from '@/components/ui/button';
import { useRouter } from 'next/router';
import Footer from '@/components/layout/Footer';
import TermsContent from './signup/TermsContent';

export default function PrivacyPage() {
	const router = useRouter();

	return (
		<>
			<Head>
				<title>개인정보 수집·이용에 대한 동의 | KEP</title>
			</Head>

			<div className="min-h-screen bg-white">
				<div className="max-w-3xl mx-auto px-4 py-8">
					<h1 className="text-2xl font-bold mb-4">
						개인정보 수집·이용에 대한 동의
					</h1>
					<p className="text-sm text-muted-foreground mb-8">
						아래 내용을 충분히 읽으신 후 동의 여부를 결정해 주세요.
					</p>

					<div className="bg-slate-50 rounded-lg p-6">
						<TermsContent />
					</div>

					<div className="mt-8">
						<Button
							className="bg-blue-800 text-white hover:bg-blue-700"
							onClick={() => router.back()}
						>
							이전 페이지로 돌아가기
						</Button>
					</div>
				</div>
			</div>

			<Footer />
		</>
	);
}
