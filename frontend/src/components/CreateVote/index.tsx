import { useState } from 'react';

import { Dialog, DialogContent, DialogFooter } from '@/components/CustomDialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';

type CreateVoteDialogProps = {
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
};

export default function CreateVoteDialog({
	isOpen,
	setIsOpen,
}: CreateVoteDialogProps) {
	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent className="w-[calc(100%-40px)] bg-slate-100 px-5 py-6 max-h-[calc(100vh-60px)] overflow-y-scroll">
				{/* 투표 생성 폼 */}
				<form className="space-y-4">
					<h3 className="text-lg font-bold">모두의 투표 만들기</h3>
					<div>
						<Label>투표 제목</Label>
						<Input type="text" />
					</div>
				</form>

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
	);
}
