import * as PortOne from '@portone/browser-sdk/v2';
import { toast } from 'sonner';

type VerificationPortOneProps = {
	redirectUrl: string; // '/signup'
};

export function useVerificationPortOne({
	redirectUrl,
}: VerificationPortOneProps) {
	PortOne.requestIdentityVerification({
		storeId: process.env.NEXT_PUBLIC_PORTONE_STORE_ID!, // 필수
		identityVerificationId: `verificationId_${Date.now()}_${Math.random()
			.toString(36)
			.substring(2, 8)}`, // 본인인증 ID
		channelKey: process.env.NEXT_PUBLIC_PORTONE_CHANNEL_KEY!, // 필수
		redirectUrl: `${window.location.origin}${redirectUrl}`, // 필수
		redirect: true,
		onError: (err: PortOne.PortOneError) => {
			console.log(err);
		},
	} as PortOne.IdentityVerificationRequest)
		.then((res) => {
			// ✅ 인증 성공시 결과 반환됨 Pc

			if (!res) {
				toast.error('인증 결과를 받아오지 못했습니다.');
				return;
			}

			// 본인인증 실패 message_(회원가입시)
			if (res.message) {
				toast.error(`${res.message}`);
				localStorage.removeItem('userInfo_phone');
				return;
			}

			// 수동 리다이렉트
			const query = new URLSearchParams({
				identityVerificationId: res.identityVerificationId,
				identityVerificationTxId: res.identityVerificationTxId,
				transactionType: res.transactionType,
			}).toString();

			window.location.href = `${redirectUrl}?${query}`;
		})
		.catch((err: PortOne.IdentityVerificationError) => {
			console.error('인증 실패', err);
			toast.error('인증 실패: ' + (err?.message || '알 수 없는 오류'));
			// 실패시 처리
		});
}
