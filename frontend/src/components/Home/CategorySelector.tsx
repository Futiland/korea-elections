import { useRouter } from 'next/router';
import { Button } from '@/components/ui/button';
import { Users, BarChart3 } from 'lucide-react';

export default function CategorySelector() {
	const router = useRouter();

	return (
		<section className="w-full max-w-5xl mx-auto px-4 pt-5">
			<div className="flex items-center justify-center gap-3">
				<Button
					variant="outline"
					size="sm"
					className="inline-flex items-center gap-2 border-slate-300 bg-white text-sm font-medium text-slate-700 hover:bg-slate-50 hover:text-slate-900"
					onClick={() => router.push('/opinion-polls')}
				>
					<BarChart3 className="h-4 w-4 text-purple-600" />
					<span>여론조사</span>
				</Button>
				<Button
					variant="outline"
					size="sm"
					className="inline-flex items-center gap-2 border-slate-300 bg-white text-sm font-medium text-slate-700 hover:bg-slate-50 hover:text-slate-900"
					onClick={() => router.push('/everyone-polls')}
				>
					<Users className="h-4 w-4 text-blue-600" />
					<span>모두의 투표</span>
				</Button>
			</div>
		</section>
	);
}
