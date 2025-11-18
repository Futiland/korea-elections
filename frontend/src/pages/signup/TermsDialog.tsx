import {
	Dialog,
	DialogContent,
	DialogFooter,
	DialogHeader,
} from '@/components/CustomDialog';
import { DialogDescription, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';

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

				<div className="text-sm space-y-3 leading-relaxed mt-4">
					<p>
						귀하의 개인정보는 회원 가입 시 본인확인을 위해 다음과 같이
						수집·이용됩니다.
					</p>
					<ul className="list-disc pl-5 space-y-1">
						<li>
							<strong>수집 항목:</strong> 성명, 생년월일, 성별, 휴대전화번호,
							중복가입확인정보(DI), 통신사 정보
						</li>
						<li>
							<strong>수집 목적:</strong> 본인확인 및 회원 가입 절차 진행
						</li>
						<li>
							<strong>보유 및 이용 기간:</strong> 회원 탈퇴 시까지 또는 관련
							법령에 따른 보존 기간
						</li>
					</ul>
					<p className="text-xs text-muted-foreground">
						※ 귀하는 개인정보 수집·이용에 대한 동의를 거부할 권리가 있습니다.
						그러나 동의를 거부할 경우 회원 가입이 제한될 수 있습니다.
					</p>
				</div>
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
