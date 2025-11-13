import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CreatePollDialog from './index';
import { toast } from 'sonner';

// Mock dependencies
jest.mock('sonner', () => ({
	toast: {
		success: jest.fn(),
		error: jest.fn(),
	},
}));

jest.mock('next/router', () => ({
	default: {
		push: jest.fn(),
	},
}));

jest.mock('@/hooks/useAuthToken', () => ({
	useAuthToken: jest.fn(() => ({
		isLoggedIn: true,
		isReady: true,
	})),
}));

describe('CreatePollDialog', () => {
	const mockSetIsOpen = jest.fn();

	const defaultProps = {
		isOpen: true,
		setIsOpen: mockSetIsOpen,
	};

	beforeEach(() => {
		jest.clearAllMocks();
	});

	describe('필수 항목 검증', () => {
		it('제목이 비어있을 때 에러 메시지를 표시해야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const titleInput =
				screen.getByPlaceholderText('투표의 제목을 작성해주세요.');
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });

			// 제목을 비우고 제출
			await user.clear(titleInput);
			await user.click(submitButton);

			await waitFor(() => {
				expect(screen.getByText('투표 제목은 필수입니다')).toBeInTheDocument();
			});
		});

		it('종료일이 없을 때 에러 메시지를 표시해야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const endAtInput = screen
				.getByLabelText(/종료일/i)
				.parentElement?.querySelector(
					'input[type="datetime-local"]'
				) as HTMLInputElement;
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });

			// 종료일을 비우고 제출
			if (endAtInput) {
				await user.clear(endAtInput);
			}
			await user.click(submitButton);

			await waitFor(() => {
				expect(screen.getByText('종료일을 선택해주세요')).toBeInTheDocument();
			});
		});

		it('종료일이 과거일 때 에러 메시지를 표시해야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const endAtInput = screen
				.getByLabelText(/종료일/i)
				.parentElement?.querySelector(
					'input[type="datetime-local"]'
				) as HTMLInputElement;
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });

			// 과거 날짜로 설정
			const pastDate = new Date(Date.now() - 24 * 60 * 60 * 1000); // 1일 전
			const pastDateString = pastDate.toISOString().slice(0, 16);
			if (endAtInput) {
				await user.clear(endAtInput);
				await user.type(endAtInput, pastDateString);
			}
			await user.click(submitButton);

			await waitFor(() => {
				expect(
					screen.getByText('종료일은 현재 이후여야 합니다')
				).toBeInTheDocument();
			});
		});

		it('단일 선택 타입에서 옵션이 2개 미만일 때 에러 메시지를 표시해야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			// formType이 이미 'single'로 설정되어 있음
			const optionInputs = screen.getAllByPlaceholderText(/옵션 \d+/);
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });

			// 옵션을 하나만 남기고 나머지 제거
			if (optionInputs.length > 1) {
				const removeButtons = screen.getAllByRole('button', { name: /제거/i });
				await user.click(removeButtons[0]);
			}

			// 마지막 옵션도 비우기
			await user.clear(optionInputs[0]);
			await user.click(submitButton);

			await waitFor(() => {
				expect(
					screen.getByText('단일/다중 선택은 최소 2개 이상의 옵션이 필요합니다')
				).toBeInTheDocument();
			});
		});

		it('다중 선택 타입에서 옵션이 2개 미만일 때 에러 메시지를 표시해야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			// formType을 'multiple'로 변경
			const formTypeSelect = screen.getByRole('combobox');
			await user.click(formTypeSelect);
			await waitFor(async () => {
				const multipleOption = screen.getByText('다중 선택');
				await user.click(multipleOption);
			});

			await waitFor(() => {
				const optionInputs = screen.getAllByPlaceholderText(/옵션 \d+/);
				expect(optionInputs.length).toBeGreaterThan(0);
			});

			const optionInputs = screen.getAllByPlaceholderText(/옵션 \d+/);
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });

			// 옵션을 하나만 남기고 나머지 제거
			if (optionInputs.length > 1) {
				const removeButtons = screen.getAllByRole('button', { name: /제거/i });
				await user.click(removeButtons[0]);
			}

			// 마지막 옵션도 비우기
			const remainingOptions = screen.getAllByPlaceholderText(/옵션 \d+/);
			if (remainingOptions.length > 0) {
				await user.clear(remainingOptions[0]);
			}
			await user.click(submitButton);

			await waitFor(() => {
				expect(
					screen.getByText('단일/다중 선택은 최소 2개 이상의 옵션이 필요합니다')
				).toBeInTheDocument();
			});
		});
	});

	describe('텍스트 길이 검증', () => {
		it('제목이 50자를 초과할 때 에러 메시지를 표시해야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const titleInput =
				screen.getByPlaceholderText('투표의 제목을 작성해주세요.');
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });

			// 51자 제목 입력
			const longTitle = 'a'.repeat(51);
			await user.clear(titleInput);
			await user.type(titleInput, longTitle);
			await user.click(submitButton);

			await waitFor(() => {
				expect(
					screen.getByText('최대 50자까지 가능합니다')
				).toBeInTheDocument();
			});
		});

		it('내용이 300자를 초과할 때 에러 메시지를 표시해야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const contentTextarea =
				screen.getByPlaceholderText('투표의 상세 내용을 작성해주세요.');
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });

			// 301자 내용 입력
			const longContent = 'a'.repeat(301);
			await user.clear(contentTextarea);
			await user.type(contentTextarea, longContent);
			await user.click(submitButton);

			await waitFor(() => {
				expect(
					screen.getByText('최대 300자까지 가능합니다')
				).toBeInTheDocument();
			});
		});

		it('옵션이 50자를 초과할 때 에러 메시지를 표시해야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const optionInputs = screen.getAllByPlaceholderText(/옵션 \d+/);
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });

			// 51자 옵션 입력
			const longOption = 'a'.repeat(51);
			await user.clear(optionInputs[0]);
			await user.type(optionInputs[0], longOption);
			await user.click(submitButton);

			await waitFor(() => {
				expect(screen.getByText('옵션은 최대 50자')).toBeInTheDocument();
			});
		});
	});

	describe('옵션 검증', () => {
		it('옵션이 2개 이상인지 확인해야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const optionInputs = screen.getAllByPlaceholderText(/옵션 \d+/);
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });

			// 옵션이 2개 이상인지 확인
			expect(optionInputs.length).toBeGreaterThanOrEqual(2);

			// 모든 옵션에 유효한 값 입력
			for (let i = 0; i < optionInputs.length; i++) {
				await user.clear(optionInputs[i]);
				await user.type(optionInputs[i], `옵션 ${i + 1}`);
			}

			// 제목과 종료일도 입력
			const titleInput =
				screen.getByPlaceholderText('투표의 제목을 작성해주세요.');
			await user.clear(titleInput);
			await user.type(titleInput, '테스트 투표');

			await user.click(submitButton);

			await waitFor(() => {
				expect(toast.success).toHaveBeenCalledWith(
					'유효성 검사를 통과했습니다. 제출 로직을 연결하세요.'
				);
			});
		});

		it('옵션이 10개를 초과할 수 없어야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const addOptionButton = screen.getByRole('button', {
				name: /옵션 추가/i,
			});

			// 옵션을 10개까지 추가
			const currentOptions = screen.getAllByPlaceholderText(/옵션 \d+/);
			const optionsToAdd = 10 - currentOptions.length;

			for (let i = 0; i < optionsToAdd; i++) {
				await user.click(addOptionButton);
			}

			// 10개가 되면 버튼이 비활성화되어야 함
			await waitFor(() => {
				expect(addOptionButton).toBeDisabled();
			});
		});

		it('빈 옵션은 허용되지 않아야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const optionInputs = screen.getAllByPlaceholderText(/옵션 \d+/);
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });

			// 옵션을 공백으로만 채우기
			await user.clear(optionInputs[0]);
			await user.type(optionInputs[0], '   '); // 공백만
			await user.click(submitButton);

			await waitFor(() => {
				expect(
					screen.getByText('빈 옵션은 허용되지 않습니다')
				).toBeInTheDocument();
			});
		});
	});

	describe('성공 케이스', () => {
		it('모든 필수 항목이 올바르게 입력되었을 때 제출이 성공해야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			// 제목 입력
			const titleInput =
				screen.getByPlaceholderText('투표의 제목을 작성해주세요.');
			await user.clear(titleInput);
			await user.type(titleInput, '테스트 투표 제목');

			// 내용 입력 (선택 사항)
			const contentTextarea =
				screen.getByPlaceholderText('투표의 상세 내용을 작성해주세요.');
			await user.clear(contentTextarea);
			await user.type(contentTextarea, '테스트 투표 내용');

			// 종료일 설정 (미래 날짜)
			const endAtInput = screen
				.getByLabelText(/종료일/i)
				.parentElement?.querySelector(
					'input[type="datetime-local"]'
				) as HTMLInputElement;
			const futureDate = new Date(Date.now() + 24 * 60 * 60 * 1000); // 1일 후
			const futureDateString = futureDate.toISOString().slice(0, 16);
			if (endAtInput) {
				await user.clear(endAtInput);
				await user.type(endAtInput, futureDateString);
			}

			// 옵션 입력
			const optionInputs = screen.getAllByPlaceholderText(/옵션 \d+/);
			for (let i = 0; i < optionInputs.length; i++) {
				await user.clear(optionInputs[i]);
				await user.type(optionInputs[i], `옵션 ${i + 1}`);
			}

			// 제출
			const submitButton = screen.getByRole('button', { name: /투표 생성/i });
			await user.click(submitButton);

			await waitFor(() => {
				expect(toast.success).toHaveBeenCalledWith(
					'유효성 검사를 통과했습니다. 제출 로직을 연결하세요.'
				);
				expect(mockSetIsOpen).toHaveBeenCalledWith(false);
			});
		});
	});

	describe('추가 기능 테스트', () => {
		it('취소 버튼을 클릭하면 다이얼로그가 닫혀야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const cancelButton = screen.getByRole('button', { name: /취소/i });
			await user.click(cancelButton);

			expect(mockSetIsOpen).toHaveBeenCalledWith(false);
		});

		it('점수제 타입일 때 옵션 필드가 표시되지 않아야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			// formType을 'score'로 변경
			const formTypeSelect = screen.getByRole('combobox');
			await user.click(formTypeSelect);
			await waitFor(async () => {
				const scoreOption = screen.getByText('점수제');
				await user.click(scoreOption);
			});

			await waitFor(() => {
				expect(screen.queryByText('옵션 설정')).not.toBeInTheDocument();
			});
		});

		it('옵션 추가 버튼을 클릭하면 새로운 옵션 필드가 추가되어야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const initialOptions = screen.getAllByPlaceholderText(/옵션 \d+/);
			const initialCount = initialOptions.length;

			const addOptionButton = screen.getByRole('button', {
				name: /옵션 추가/i,
			});
			await user.click(addOptionButton);

			await waitFor(() => {
				const newOptions = screen.getAllByPlaceholderText(/옵션 \d+/);
				expect(newOptions.length).toBe(initialCount + 1);
			});
		});

		it('옵션 제거 버튼을 클릭하면 해당 옵션이 제거되어야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const initialOptions = screen.getAllByPlaceholderText(/옵션 \d+/);
			const initialCount = initialOptions.length;

			const removeButtons = screen.getAllByRole('button', { name: /제거/i });
			await user.click(removeButtons[0]);

			await waitFor(() => {
				const newOptions = screen.getAllByPlaceholderText(/옵션 \d+/);
				expect(newOptions.length).toBe(initialCount - 1);
			});
		});

		it('재투표 가능 여부 스위치를 토글할 수 있어야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const revoteSwitch = screen.getByRole('switch');
			expect(revoteSwitch).not.toBeChecked();

			await user.click(revoteSwitch);

			await waitFor(() => {
				expect(revoteSwitch).toBeChecked();
			});
		});

		it('제출 중일 때 버튼이 비활성화되어야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			// 모든 필수 항목 입력
			const titleInput =
				screen.getByPlaceholderText('투표의 제목을 작성해주세요.');
			await user.clear(titleInput);
			await user.type(titleInput, '테스트 투표');

			const optionInputs = screen.getAllByPlaceholderText(/옵션 \d+/);
			for (let i = 0; i < optionInputs.length; i++) {
				await user.clear(optionInputs[i]);
				await user.type(optionInputs[i], `옵션 ${i + 1}`);
			}

			const submitButton = screen.getByRole('button', { name: /투표 생성/i });
			await user.click(submitButton);

			// 제출 중 상태 확인 (toast가 호출되기 전까지)
			await waitFor(() => {
				expect(submitButton).toBeDisabled();
			});
		});

		it('제목과 내용의 글자 수가 실시간으로 표시되어야 한다', async () => {
			const user = userEvent.setup();
			render(<CreatePollDialog {...defaultProps} />);

			const titleInput =
				screen.getByPlaceholderText('투표의 제목을 작성해주세요.');
			const contentTextarea =
				screen.getByPlaceholderText('투표의 상세 내용을 작성해주세요.');

			await user.clear(titleInput);
			await user.type(titleInput, '테스트');

			await waitFor(() => {
				expect(screen.getByText('3/50')).toBeInTheDocument();
			});

			await user.clear(contentTextarea);
			await user.type(contentTextarea, '내용 테스트');

			await waitFor(() => {
				expect(screen.getByText('6/300')).toBeInTheDocument();
			});
		});
	});
});
