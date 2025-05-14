import Link from 'next/link';

export default function Footer() {
	return (
		<footer className="w-full border-t bg-white dark:bg-gray-950">
			<div className="mx-auto max-w-7xl px-4 py-6 text-sm text-muted-foreground flex flex-col md:flex-row justify-between items-center gap-2">
				<div>
					&copy; {new Date().getFullYear()} Korea Elections. All rights reserved
				</div>
				<div className="flex gap-4">
					<Link href="/privacy" className="hover:underline">
						개인정보처리방침
					</Link>
					<Link href="/terms" className="hover:underline">
						이용약관
					</Link>
				</div>
			</div>
		</footer>
	);
}
