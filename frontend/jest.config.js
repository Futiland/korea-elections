const nextJest = require('next/jest');

const createJestConfig = nextJest({
	// Next.js 앱의 경로를 제공하여 next.config.js와 .env 파일을 로드
	dir: './',
});

// Jest에 전달할 사용자 정의 설정
const customJestConfig = {
	setupFilesAfterEnv: ['<rootDir>/src/test/setup.ts'],
	testEnvironment: 'jsdom',
	moduleNameMapper: {
		'^@/(.*)$': '<rootDir>/src/$1',
	},
	testPathIgnorePatterns: ['<rootDir>/.next/', '<rootDir>/node_modules/'],
	collectCoverageFrom: [
		'src/**/*.{js,jsx,ts,tsx}',
		'!src/**/*.d.ts',
		'!src/test/**',
	],
};

// createJestConfig는 next/jest가 비동기적으로 Next.js 구성을 로드할 수 있도록 하는 함수
module.exports = createJestConfig(customJestConfig);
