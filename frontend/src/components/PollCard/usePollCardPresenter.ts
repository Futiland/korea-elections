import { useCallback, useEffect, useMemo, useState } from 'react';
import {
	useIsFetching,
	useMutation,
	useQueryClient,
} from '@tanstack/react-query';
import { toast } from 'sonner';

import { submitPublicPoll } from '@/lib/api/poll';
import {
	PublicPollData,
	QuestionType,
	type PublicPollSubmitResponse,
} from '@/lib/types/poll';
import { getDateRangeDurationLabel } from '@/lib/date';
import { getParticipationMessage } from '@/lib/utils';
import { useRequireLogin } from '@/hooks/useRequireLogin';
import { googleAnalyticsCustomEvent } from '@/lib/gtag';

declare global {
	interface Window {
		Kakao?: {
			init: (key: string) => void;
			isInitialized: () => boolean;
			Share?: {
				sendDefault: (config: Record<string, unknown>) => void;
			};
		};
	}
}

export interface PollCardPresenterProps {
	pollData: PublicPollData;
}

export interface PollCardViewProps {
	pollData: PublicPollData;
	showResults: boolean;
	isRefreshingPolls: boolean;
	participationMessage: string;
	remainingTimeLabel: string | null;
	isShareDialogOpen: boolean;
	setIsShareDialogOpen: (isOpen: boolean) => void;
	shareUrl: string;
	selectedOptionValue: number[] | number;
	onChangeSelectedOption: (value: number[] | number) => void;
	onClickShare: () => void;
	onCopyShareUrl: () => void;
	onShareKakao: () => void;
	onClickShowResults: (show: boolean, resultBtnType: string) => void;
	onSubmitPoll: () => void;
	isSubmittingPoll: boolean;
	isLoggedIn: boolean;
}

export function usePollCardPresenter({
	pollData,
}: PollCardPresenterProps): PollCardViewProps {
	const [showResults, setShowResults] = useState(false);
	const [selectedOptionValue, setSelectedOptionValue] = useState<
		number[] | number
	>([]);
	const [isShareDialogOpen, setIsShareDialogOpen] = useState(false);
	const [currentOrigin, setCurrentOrigin] = useState('');
	const { ensureLoggedIn, isLoggedIn } = useRequireLogin();

	const queryClient = useQueryClient();
	const isRefreshingPolls = useIsFetching({ queryKey: ['publicPolls'] }) > 0;

	const participationMessage = getParticipationMessage(
		pollData?.status,
		pollData?.responseCount
	);

	const remainingTimeLabel = pollData?.endAt
		? getDateRangeDurationLabel(pollData.endAt)
		: null;

	const submitPublicPollMutation = useMutation({
		mutationFn: (payload: {
			pollId: number;
			optionId: number[] | number;
			responseType: QuestionType;
		}) =>
			submitPublicPoll(payload.pollId, payload.optionId, payload.responseType),
		onSuccess: () => {
			toast.success('투표가 완료되었습니다.');
			// 목록 쿼리 갱신
			queryClient.invalidateQueries({ queryKey: ['publicPolls'] });
			queryClient.invalidateQueries({ queryKey: ['opinionPolls'] });

			// 개별 poll 쿼리도 갱신하여 결과를 볼 수 있게 함
			if (pollData?.id) {
				queryClient.invalidateQueries({
					queryKey: ['publicPoll', pollData.id],
				});
				queryClient.invalidateQueries({
					queryKey: ['opinionPoll', pollData.id],
				});
			}
		},
		onError: (data: PublicPollSubmitResponse) => {
			toast.error(data.message);
		},
	});

	useEffect(() => {
		if (typeof window === 'undefined') return;
		setCurrentOrigin(window.location.origin);
	}, []);

	useEffect(() => {
		if (typeof window === 'undefined') return;
		const kakaoKey = process.env.NEXT_PUBLIC_KAKAO_JAVASCRIPT_KEY;
		if (!kakaoKey) return;

		if (window.Kakao) {
			if (!window.Kakao.isInitialized()) {
				window.Kakao.init(kakaoKey);
			}
			return;
		}

		const existingScript = document.getElementById('kakao-sdk');
		if (existingScript) return;

		const script = document.createElement('script');
		script.id = 'kakao-sdk';
		script.src = 'https://developers.kakao.com/sdk/js/kakao.js';
		script.onload = () => {
			if (window.Kakao && !window.Kakao.isInitialized()) {
				window.Kakao.init(kakaoKey);
			}
		};
		document.head.appendChild(script);
	}, []);

	const sharePath = useMemo(() => {
		if (!pollData?.id) return '';
		return `everyone-polls/${pollData.id}`;
	}, [pollData?.id]);

	const shareUrl = useMemo(() => {
		if (!sharePath) return '';
		return currentOrigin ? `${currentOrigin}/${sharePath}` : sharePath;
	}, [currentOrigin, sharePath]);

	const onClickShare = useCallback(() => {
		if (!pollData?.id) return;
		setIsShareDialogOpen(true);
		googleAnalyticsCustomEvent({
			action: 'poll_card_share_button_click',
			category: 'poll_share',
			label: pollData?.title ?? '',
			value: pollData?.id,
		});
	}, [pollData?.id]);

	const onCopyShareUrl = useCallback(async () => {
		if (!shareUrl) return;

		try {
			await navigator.clipboard.writeText(shareUrl);
			toast.success('공유 링크가 복사되었습니다.');
		} catch {
			toast.error('링크 복사에 실패했어요. 다시 시도해주세요.');
		}
	}, [shareUrl]);

	const onShareKakao = useCallback(() => {
		if (typeof window === 'undefined' || !shareUrl) return;
		const kakao = window.Kakao;

		if (!kakao || !kakao.isInitialized() || !kakao.Share) {
			toast.error(
				'카카오톡 공유를 준비하지 못했어요. 잠시 후 다시 시도해주세요.'
			);
			return;
		}

		// sendDefault를 사용하여 카카오톡 앱 열기
		// kakaolink:// 스킴 오류는 브라우저 콘솔에만 표시되고 실제 기능에는 영향 없음
		// 콘솔 오류를 조용히 처리하기 위해 console.error를 일시적으로 비활성화
		const originalConsoleError = console.error;
		console.error = () => {}; // 일시적으로 에러 로깅 비활성화

		try {
			kakao.Share.sendDefault({
				objectType: 'text',
				text: `[모두의 투표] ${pollData?.title ?? '투표에 참여해보세요!'}`,
				link: {
					mobileWebUrl: shareUrl,
					webUrl: shareUrl,
				},
				buttonTitle: '투표하러 가기',
			});

			// 짧은 시간 후 console.error 복원
			setTimeout(() => {
				console.error = originalConsoleError;
			}, 100);
		} catch (error) {
			console.error = originalConsoleError; // 에러 발생 시 즉시 복원
			console.error('카카오톡 공유 오류:', error);
			toast.warning(
				'카카오톡 공유에 실패했습니다. 링크를 복사해서 공유해주세요.',
				{ duration: 3000 }
			);
			onCopyShareUrl();
		}

		googleAnalyticsCustomEvent({
			action: 'poll_card_share_kakao_button_click',
			category: 'poll_share',
			label: pollData?.title ?? '',
			value: pollData?.id,
		});
	}, [pollData?.title, shareUrl, onCopyShareUrl]);

	const onClickShowResults = useCallback(
		(show: boolean, resultBtnType: string) => {
			ensureLoggedIn({
				onSuccess: () => setShowResults(show),
				description: '로그인 후 투표 결과를 확인해보세요. 😃',
			});

			googleAnalyticsCustomEvent({
				action: `poll_card_${resultBtnType}_button_click`,
				category: 'poll_show_results',
				label: pollData?.title ?? '',
				value: pollData?.id,
			});
		},
		[ensureLoggedIn]
	);

	const onSubmitPoll = useCallback(() => {
		if (
			Array.isArray(selectedOptionValue) &&
			selectedOptionValue.length === 0
		) {
			toast.error('투표 옵션을 선택해주세요.');
			return;
		}

		const submitPoll = () =>
			submitPublicPollMutation.mutate({
				pollId: pollData?.id,
				optionId: selectedOptionValue,
				responseType: pollData?.responseType,
			});

		ensureLoggedIn({
			onSuccess: submitPoll,
			description: '투표 참여는 로그인 후 가능합니다.',
		});

		googleAnalyticsCustomEvent({
			action: 'submit_poll',
			category: 'poll',
			label: pollData?.title ?? '',
			value: pollData?.id,
		});
	}, [
		ensureLoggedIn,
		pollData?.id,
		pollData?.responseType,
		selectedOptionValue,
		submitPublicPollMutation,
	]);

	return {
		pollData,
		showResults,
		isRefreshingPolls,
		participationMessage,
		remainingTimeLabel,
		isShareDialogOpen,
		setIsShareDialogOpen,
		shareUrl,
		selectedOptionValue,
		onChangeSelectedOption: setSelectedOptionValue,
		onClickShare,
		onCopyShareUrl,
		onShareKakao,
		onClickShowResults,
		onSubmitPoll,
		isSubmittingPoll:
			submitPublicPollMutation.isPending || Boolean(isRefreshingPolls),
		isLoggedIn,
	};
}
