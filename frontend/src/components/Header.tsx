import Link from 'next/link';
import { useRouter } from 'next/router';
import { Button } from '@/components/ui/button';
import {
	DropdownMenu,
	DropdownMenuTrigger,
	DropdownMenuContent,
	DropdownMenuItem,
} from '@/components/ui/dropdown-menu';
import { Menu } from 'lucide-react';
import Image from 'next/image';

export default function Header() {
	const router = useRouter();

	return (
		<header className="w-full border-b bg-slate-100 dark:bg-gray-950">
			<div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3">
				{/* 로고 */}
				<Link href="/">
					<Image
						src="/img/logo-g.svg" // public 폴더 기준
						alt="KEP 로고"
						width={76}
						height={24}
					/>
				</Link>

				{/* 우측 */}
				<div className="flex items-center gap-2">
					<Button variant="outline" size="sm" className="py-1 px-4">
						KEP 란?
					</Button>
				</div>
			</div>
		</header>
	);
}
