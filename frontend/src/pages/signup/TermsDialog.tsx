import {
	Dialog,
	DialogContent,
	DialogFooter,
	DialogHeader,
} from '@/components/CustomDialog';
import { DialogDescription, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import TermsContent from './TermsContent';

type TermsDialogProps = {
	isOpen: boolean;
	setIsOpen: (isVisible: boolean) => void;
};

export default function TermsDialog({ isOpen, setIsOpen }: TermsDialogProps) {
	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent className="w-[calc(100%-40px)] bg-slate-100 px-5 py-6">
				<DialogHeader>
					<DialogTitle>개인정보 수집·이용에 대한 동의</DialogTitle>
					<DialogDescription>
						아래 내용을 충분히 읽으신 후 동의 여부를 결정해 주세요.
					</DialogDescription>
				</DialogHeader>

				<TermsContent />

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
