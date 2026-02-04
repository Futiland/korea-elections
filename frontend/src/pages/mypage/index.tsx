import { useMyPagePresenter } from '@/components/My/useMyPagePresenter';
import MyPageView from '@/components/My/MyPageView';

export default function MyPage() {
	const presenterProps = useMyPagePresenter();
	return <MyPageView {...presenterProps} />;
}
