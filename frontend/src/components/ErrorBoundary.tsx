import { ReactNode } from 'react';
import { FallbackProps } from 'react-error-boundary';

interface CustomErrorFallbackProps extends FallbackProps {}

export function ErrorFallback({
	error,
	resetErrorBoundary,
}: CustomErrorFallbackProps) {
	return (
		<div style={{ padding: 20, textAlign: 'center' }}>
			<h2>문제가 발생했습니다 😢</h2>
			<p>{error.message}</p>
			<button onClick={resetErrorBoundary}>다시 시도하기</button>
		</div>
	);
}
