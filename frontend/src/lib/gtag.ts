export const GA_ID = process.env.NEXT_PUBLIC_GOOGLE_TAG_MANAGER_ID;

// Google Analytics gtag 타입 정의
declare global {
	interface Window {
		gtag: (
			command: 'config' | 'event' | 'js' | 'set',
			targetId: string | Date,
			config?: {
				page_path?: string;
				[key: string]: unknown;
			}
		) => void;
		dataLayer: unknown[];
	}
}

// 페이지뷰
export const googleAnalyticsPageview = (url: string) => {
	if (typeof GA_ID === 'string' && GA_ID) {
		window.gtag('config', GA_ID, {
			page_path: url,
		});
	}
};

// 커스텀 이벤트
export const googleAnalyticsCustomEvent = ({
	action,
	category,
	label,
	value,
}: any) => {
	window.gtag('event', action, {
		event_category: category,
		event_label: label,
		value,
	});
};
