// components/ui/spinner.tsx

// import { Clock } from 'lucide-react';
import { Loader2 } from 'lucide-react';
// import { Orbit } from 'lucide-react';

export function Spinner() {
	return (
		<div className="flex h-[60vh] items-center justify-center">
			<Loader2 className="h-10 w-10 animate-spin text-blue-900" />
		</div>
	);
}
