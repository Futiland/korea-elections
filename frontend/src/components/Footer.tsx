import Link from 'next/link';
import { Phone, Building2 } from 'lucide-react';

export default function Footer() {
	return (
		<footer className="w-full border-t bg-white dark:bg-gray-950">
			<div className="mx-auto max-w-7xl px-4 py-6 mb-14 text-sm text-muted-foreground flex flex-col md:flex-row justify-between items-center gap-2">
				<div className="flex gap-4">
					<div className="flex gap-2 justify-center items-center">
						<Phone className="w-4 h-4" />
						<p>0507-1322-0033</p>
					</div>
					<div className="flex gap-2 justify-center items-center">
						<Building2 className="w-4 h-4" />
						<p>105-60-00842 (사업자등록번호)</p>
					</div>
				</div>
				<div className="flex gap-4">
					<Link href="/privacy" className="hover:underline">
						개인정보처리방침
					</Link>
					<Link href="/terms" className="hover:underline">
						이용약관
					</Link>
				</div>
				<div>
					&copy; {new Date().getFullYear()} Korea Elections. All rights reserved
				</div>
			</div>
		</footer>
	);
}
