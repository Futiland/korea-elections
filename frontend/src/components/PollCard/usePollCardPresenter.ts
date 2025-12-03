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
import { addCommas } from '@/lib/utils';
import { useRequireLogin } from '@/hooks/useRequireLogin';

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
	onClickShowResults: (show: boolean) => void;
	onSubmitPoll: () => void;
	isSubmittingPoll: boolean;
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
	const { ensureLoggedIn } = useRequireLogin();

	const queryClient = useQueryClient();
	const isRefreshingPolls = useIsFetching({ queryKey: ['publicPolls'] }) > 0;

	const isExpired = pollData?.status === 'EXPIRED';
	const hasParticipants =
		!!pollData?.responseCount && pollData.responseCount > 0;

	const participationMessage = isExpired
		? hasParticipants
			? `총 ${addCommas(pollData.responseCount)}명이 참여했습니다.`
			: '아쉽게도 참여자가 없었습니다. 😢'
		: hasParticipants
		? `지금까지 ${addCommas(pollData.responseCount)}명이 참여했어요!`
		: '첫 번째 참여자가 되어 주세요!';

	const remainingTimeLabel =
		pollData?.startAt && pollData?.endAt
			? getDateRangeDurationLabel(pollData.startAt, pollData.endAt)
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
			queryClient.invalidateQueries({ queryKey: ['publicPolls'] });
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
		return `korea-election/everyone-poll/${pollData.id}`;
	}, [pollData?.id]);

	const shareUrl = useMemo(() => {
		if (!sharePath) return '';
		return currentOrigin ? `${currentOrigin}/${sharePath}` : sharePath;
	}, [currentOrigin, sharePath]);

	const onClickShare = useCallback(() => {
		if (!pollData?.id) return;
		setIsShareDialogOpen(true);
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
		} catch (error) {
			console.error(error);
			toast.error('카카오톡 공유 중 오류가 발생했습니다.');
		}
	}, [pollData?.title, shareUrl]);

	const onClickShowResults = useCallback(
		(show: boolean) =>
			ensureLoggedIn({
				onSuccess: () => setShowResults(show),
				description: '투표 결과를 확인하려면 로그인이 필요합니다.',
			}),
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
	};
}
