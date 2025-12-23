import '@testing-library/jest-dom';

// Mock Next.js router
const mockPush = jest.fn().mockResolvedValue(true);
const mockReplace = jest.fn().mockResolvedValue(true);
const mockBack = jest.fn();
const mockPrefetch = jest.fn().mockResolvedValue(undefined);
const mockReload = jest.fn();

const defaultRouter = {
	route: '/',
	pathname: '/',
	query: {},
	asPath: '/',
	push: mockPush,
	replace: mockReplace,
	back: mockBack,
	reload: mockReload,
	prefetch: mockPrefetch,
	beforePopState: jest.fn(),
	events: {
		on: jest.fn(),
		off: jest.fn(),
		emit: jest.fn(),
	},
	isReady: true,
	isFallback: false,
	isLocaleDomain: false,
	isPreview: false,
};

jest.mock('next/router', () => ({
	useRouter: jest.fn(() => defaultRouter),
}));

// Mock Next.js Image component
jest.mock('next/image', () => ({
	default: (props: any) => {
		return null;
	},
}));

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
	writable: true,
	value: jest.fn().mockImplementation((query) => ({
		matches: false,
		media: query,
		onchange: null,
		addListener: jest.fn(), // deprecated
		removeListener: jest.fn(), // deprecated
		addEventListener: jest.fn(),
		removeEventListener: jest.fn(),
		dispatchEvent: jest.fn(),
	})),
});

// Mock ResizeObserver
global.ResizeObserver = jest.fn().mockImplementation(() => ({
	observe: jest.fn(),
	unobserve: jest.fn(),
	disconnect: jest.fn(),
}));
