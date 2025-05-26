import Link from 'next/link';
import { useRouter } from 'next/router';
import { Button } from '@/components/ui/button';
import { Menu } from 'lucide-react';
import Image from 'next/image';
import { useState } from 'react';

import IntroduceLayout from '@/components/IntroduceLayout';
import { Dialog, DialogContent, DialogFooter } from '@/components/CustomDialog';

export default function Header() {
	const router = useRouter();

	const [isOpen, setIsOpen] = useState(false);

	const visbleIntroduceDialog = () => {
		setIsOpen(true);
	};

	return (
		<header className="w-full border-b bg-slate-50 dark:bg-gray-950 shadow-sm">
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
				<div className="flex items-center gap-2">
					<Button
						variant="outline"
						size="sm"
						className="py-1 px-4"
						onClick={visbleIntroduceDialog}
					>
						KEP 프로젝트 소개
					</Button>
				</div>
			</div>

			<Dialog open={isOpen} onOpenChange={setIsOpen}>
				<DialogContent className="w-[calc(100%-40px)] bg-slate-100 px-5 py-6">
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
