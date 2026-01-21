import { useCallback } from 'react';
import { useRouter } from 'next/router';
import { useMutation, useQuery } from '@tanstack/react-query';
import { toast } from 'sonner';

import { useAuthToken } from '@/hooks/useAuthToken';
import { useAlertDialog } from '@/components/providers/AlertDialogProvider';
import { getUserInfo, deleteAccount, getStats } from '@/lib/api/account';
import {
	getMyPolls,
	getMyParticipatedPublicPolls,
	getMyParticipatedOpinionPolls,
} from '@/lib/api/poll';
import type { StatsData, StatsResponse, UserInfo } from '@/lib/types/account';
import type { MyPollData, MyPollResponse } from '@/lib/types/poll';

export interface MyPageViewProps {
	user: UserInfo | undefined;
	isLoading: boolean;
	isError: boolean;
	isLoggedIn: boolean;
	isReady: boolean;
	isDeleting: boolean;
	onLogout: () => void;
	onDeleteAccount: () => void;
	myPolls: { content: MyPollData[]; totalCount: number; totalPages: number };
	myParticipatedPublicPolls: {
		content: MyPollData[];
		totalCount: number;
		totalPages: number;
	};
	myParticipatedOpinionPolls: {
		content: MyPollData[];
		totalCount: number;
		totalPages: number;
	};
	isFetchingMyPolls: boolean;
	isFetchingMyParticipatedPublicPolls: boolean;
	isFetchingMyParticipatedOpinionPolls: boolean;
	isErrorMyPolls: boolean;
	isErrorMyParticipatedPublicPolls: boolean;
	isErrorMyParticipatedOpinionPolls: boolean;
	isFetchingStats: boolean;
	isErrorStats: boolean;
	stats: StatsData;
}

export function useMyPagePresenter(): MyPageViewProps {
	const router = useRouter();
	const { showDialog, hideDialog } = useAlertDialog();
	const { isLoggedIn, isReady } = useAuthToken();

	const {
		data: user,
		isFetching,
		isError,
	} = useQuery<UserInfo>({
		queryKey: ['userInfo'],
		queryFn: getUserInfo,
		enabled: isLoggedIn && isReady,
		refetchOnWindowFocus: false,
		retry: 2,
	});

	const {
		data: stats,
		isFetching: isFetchingStats,
		isError: isErrorStats,
	} = useQuery<StatsResponse>({
		queryKey: ['stats'],
		queryFn: getStats,
		enabled: isLoggedIn && isReady,
		retry: 2,
	});

	const {
		data: myPolls,
		isFetching: isFetchingMyPolls,
		isError: isErrorMyPolls,
	} = useQuery<MyPollResponse>({
		queryKey: ['myPolls'],
		queryFn: () => getMyPolls({ size: 2, page: 1 }),
		enabled: isLoggedIn && isReady,
		retry: 2,
	});

	const {
		data: myParticipatedPublicPolls,
		isFetching: isFetchingMyParticipatedPublicPolls,
		isError: isErrorMyParticipatedPublicPolls,
	} = useQuery<MyPollResponse>({
		queryKey: ['myParticipatedPublicPolls'],
		queryFn: () => getMyParticipatedPublicPolls({ size: 2, page: 1 }),
		enabled: isLoggedIn && isReady,
		retry: 2,
	});

	const {
		data: myParticipatedOpinionPolls,
		isFetching: isFetchingMyParticipatedOpinionPolls,
		isError: isErrorMyParticipatedOpinionPolls,
	} = useQuery<MyPollResponse>({
		queryKey: ['myParticipatedOpinionPolls'],
		queryFn: () => getMyParticipatedOpinionPolls({ size: 2, page: 1 }),
		enabled: isLoggedIn && isReady,
		retry: 2,
	});

	const logoutHandler = useCallback(() => {
		localStorage.removeItem('token');
		router.push({
			pathname: '/login',
			query: { redirect: router.asPath },
		});
	}, [router]);

	const deleteMutation = useMutation({
		mutationFn: deleteAccount,
		onSuccess: () => {
			toast.success('회원 탈퇴가 완료되었습니다.');
			localStorage.removeItem('token');
			router.replace('/');
		},
		onError: (error: any) => {
			toast.error(error?.message || '회원 탈퇴 중 오류가 발생했습니다.');
		},
	});

	const handleDelete = useCallback(() => {
		showDialog({
			message: '정말로 탈퇴하시겠어요?',
			discription:
				'탈퇴 시 모든 데이터가 삭제되며 복구가 불가능하며, 30일 후 재가입이 가능합니다.',
			actions: (
				<div className="flex gap-2 w-full">
					<button
						className="w-1/2 bg-slate-200 text-slate-900 hover:bg-slate-200 rounded-md px-4 py-2"
						onClick={() => hideDialog()}
					>
						취소
					</button>
					<button
						className="w-1/2 bg-red-600 hover:bg-red-500 text-white rounded-md px-4 py-2"
						onClick={() => {
							deleteMutation.mutate();
							hideDialog();
						}}
						disabled={deleteMutation.isPending}
					>
						{deleteMutation.isPending ? '처리 중...' : '회원 탈퇴'}
					</button>
				</div>
			),
		});
	}, [showDialog, hideDialog, deleteMutation]);

	return {
		user,
		isLoading: isFetching || !isReady,
		isError,
		isLoggedIn,
		isReady,
		isDeleting: deleteMutation.isPending,
		onLogout: logoutHandler,
		onDeleteAccount: handleDelete,
		myPolls: myPolls?.data ?? { content: [], totalCount: 0, totalPages: 0 },
		myParticipatedPublicPolls: myParticipatedPublicPolls?.data ?? {
			content: [],
			totalCount: 0,
			totalPages: 0,
		},
		myParticipatedOpinionPolls: myParticipatedOpinionPolls?.data ?? {
			content: [],
			totalCount: 0,
			totalPages: 0,
		},
		isFetchingMyPolls,
		isFetchingMyParticipatedPublicPolls,
		isFetchingMyParticipatedOpinionPolls,
		isErrorMyPolls,
		isErrorMyParticipatedPublicPolls,
		isErrorMyParticipatedOpinionPolls,
		isFetchingStats,
		isErrorStats,
		stats: stats?.data ?? {
			createdPollCount: 0,
			participatedPublicPollCount: 0,
			participatedSystemPollCount: 0,
		},
	};
}
