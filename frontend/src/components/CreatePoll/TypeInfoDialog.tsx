import { Dialog, DialogContent } from '@/components/CustomDialog';
import { Button } from '@/components/ui/button';
import SingleChoiceOption from '@/components/PollOptions/SingleChoiceOption';
import MultipleChoiceOption from '@/components/PollOptions/MultipleChoiceOption';
import ScoreOption from '@/components/PollOptions/ScoreOption';
import type { OptionData } from '@/lib/types/poll';

const SAMPLE_OPTIONS: OptionData[] = [
	{ id: 1, optionText: '옵션 A', optionOrder: 1 },
	{ id: 2, optionText: '옵션 B', optionOrder: 2 },
	{ id: 3, optionText: '옵션 C', optionOrder: 3 },
];

interface TypeInfoDialogProps {
	open: boolean;
	onOpenChange: (open: boolean) => void;
}

export function TypeInfoDialog({ open, onOpenChange }: TypeInfoDialogProps) {
	return (
		<Dialog open={open} onOpenChange={onOpenChange}>
			<DialogContent className="w-[calc(100%-40px)] max-w-md bg-white px-5 py-6">
				<div className="space-y-3">
					<div className="flex flex-col gap-1 mb-4">
						<h3 className="text-lg font-semibold">모두의 투표 폼 타입 안내</h3>
						<p className="text-xs text-slate-700">
							원하는 옵션 타입을 선택해 주세요.
						</p>
					</div>
					<div className="space-y-4 text-sm text-slate-700">
						<div className="space-y-2 rounded-md border border-slate-200 bg-slate-50/60 p-3">
							<div>
								<p className="mb-1">
									<span className="font-semibold text-blue-700">단일 선택</span>{' '}
									_ 한 가지만 선택할 수 있어요.
								</p>
								<p className="text-xs text-slate-600 mt-1">
									예시: 동의/비동의, 매우좋음/좋음/보통/나쁨/매우나쁨, 찬성/반대
								</p>
							</div>
							<div className="rounded-md border border-slate-200 bg-white p-3">
								<SingleChoiceOption
									pollId={0}
									options={SAMPLE_OPTIONS}
									value={SAMPLE_OPTIONS[0].id.toString()}
									onValueChange={() => {}}
									isVoted
								/>
							</div>
						</div>

						<div className="space-y-2 rounded-md border border-slate-200 bg-slate-50/60 p-3">
							<div>
								<p className="mb-1">
									<span className="font-semibold text-blue-700">다중 선택</span>{' '}
									_ 여러 개를 동시에 선택할 수 있어요.
								</p>
								<p className="text-xs text-slate-600 mt-1">
									예시: 관심 있는 주제를 여러 개 선택 (IT/경제/정치/스포츠 등)
								</p>
							</div>
							<div className="rounded-md border border-slate-200 bg-white p-3">
								<MultipleChoiceOption
									options={SAMPLE_OPTIONS}
									selectedValues={[SAMPLE_OPTIONS[0].id.toString()]}
									onChange={() => {}}
									isVoted
								/>
							</div>
						</div>

						<div className="space-y-2 rounded-md border border-slate-200 bg-slate-50/60 p-3">
							<div>
								<p className="mb-1">
									<span className="font-semibold text-blue-700">점수제</span> _
									질문에 대해 0점부터 10점까지 점수로 평가할 수 있어요.
								</p>
								<p className="text-xs text-slate-600 mt-1">
									예시: "사업 방향성에 대한 나의 점수는 10점 만점에 몇
									점인가요?"
									<br />
									"이번 프로젝트의 만족도는 10점 만점에 몇 점인가요?"
								</p>
							</div>
							<div className="rounded-md border border-slate-200 bg-white p-3">
								<ScoreOption
									maxScore={10}
									selectedScore={7}
									onChange={() => {}}
									isVoted
								/>
							</div>
						</div>
					</div>

					<div className="flex justify-end">
						<Button
							type="button"
							variant="default"
							className="h-9 px-4 bg-blue-800 text-white hover:bg-blue-700"
							onClick={() => onOpenChange(false)}
						>
							확인
						</Button>
					</div>
				</div>
			</DialogContent>
		</Dialog>
	);
}
