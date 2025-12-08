import { useMyPagePresenter } from './useMyPagePresenter';
import MyPageView from './MyPageView';

export default function MyPage() {
	const presenterProps = useMyPagePresenter();
	return <MyPageView {...presenterProps} />;
}
