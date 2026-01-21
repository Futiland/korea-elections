import {
	Dialog,
	DialogContent,
	DialogFooter,

} from '@/components/CustomDialog';

import { Button } from '@/components/ui/button';
import PrivacyContents from '@/components/Policy/PrivacyContents';

type PrivacyDialogProps = {
	isOpen: boolean;
	setIsOpen: (isVisible: boolean) => void;
};

export default function PrivacyDialog({ isOpen, setIsOpen }: PrivacyDialogProps) {
	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen} >
			<DialogContent
				className="w-[calc(100%-40px)] bg-slate-100 px-5 py-6 max-h-[calc(100vh-60px)] overflow-y-scroll	">

				<PrivacyContents />

				<DialogFooter>
					<Button
						className="w-full bg-blue-800 text-white hover:bg-blue-700"
						onClick={() => setIsOpen(false)}
					>
						확인
					</Button>
				</DialogFooter>
			</DialogContent>
		</Dialog>
	);
}
