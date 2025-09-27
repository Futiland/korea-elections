import Link from 'next/link';
import { Button } from '@/components/ui/button';
import Image from 'next/image';
import { useState } from 'react';

import IntroduceLayout from '@/components/IntroduceLayout';
import { Dialog, DialogContent, DialogFooter } from '@/components/CustomDialog';
import { User, Info, UserRound } from 'lucide-react';

export default function Header() {
	const [isOpen, setIsOpen] = useState(false);

	const visbleIntroduceDialog = () => {
		setIsOpen(true);
	};

	return (
		<header className="w-full border-b bg-slate-20 dark:bg-gray-950 shadow-sm">
			<div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3">
				{/* 로고 */}
				<Link href="/">
					<div className="relative w-[76px] h-[24px]">
						<Image
							src="/img/logo-g.svg"
							alt="KEP 로고"
							fill
							className="object-contain"
						/>
					</div>
				</Link>

				{/* 우측 */}
				<div className="flex items-center gap-4">
					{/* KEP 프로젝트 소개 */}
					<Info className="w-6 h-6" onClick={visbleIntroduceDialog} />

					<Link href="/mypage">
						<UserRound className="w-6 h-6" />
					</Link>
				</div>
			</div>

			<Dialog open={isOpen} onOpenChange={setIsOpen}>
				<DialogContent className="w-[calc(100%-40px)] bg-slate-100 px-5 py-6 max-h-[calc(100vh-60px)] overflow-y-scroll">
					<IntroduceLayout />
					<DialogFooter>
						<Button
							className="w-full bg-blue-900 text-white hover:bg-blue-800"
							onClick={() => setIsOpen(false)}
						>
							확인
						</Button>
					</DialogFooter>
				</DialogContent>
			</Dialog>
		</header>
	);
}
