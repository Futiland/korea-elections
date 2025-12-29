import { useEffect, useState } from 'react';
import { ArrowUp } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface TopButtonProps {
	showAfter?: number; // 스크롤 위치 (px) 이상일 때 표시
	className?: string;
}

export default function TopButton({
	showAfter = 400,
	className,
}: TopButtonProps) {
	const [isVisible, setIsVisible] = useState(false);

	useEffect(() => {
		const handleScroll = () => {
			const scrollY = window.scrollY || document.documentElement.scrollTop;
			setIsVisible(scrollY > showAfter);
		};

		window.addEventListener('scroll', handleScroll);
		handleScroll(); // 초기 상태 확인

		return () => {
			window.removeEventListener('scroll', handleScroll);
		};
	}, [showAfter]);

	const scrollToTop = () => {
		window.scrollTo({
			top: 0,
			behavior: 'smooth',
		});
	};

	if (!isVisible) {
		return null;
	}

	return (
		<Button
			type="button"
			variant="default"
			size="icon"
			onClick={scrollToTop}
			className={cn(
				'w-14 h-14 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 bg-white hover:bg-slate-100 text-slate-700 border border-slate-200 opacity-80 hover:opacity-100 [&_svg]:!w-8 [&_svg]:!h-8',
				className
			)}
			aria-label="맨 위로 이동"
		>
			<ArrowUp />
		</Button>
	);
}
