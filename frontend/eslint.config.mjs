// eslint.config.mjs
import js from '@eslint/js';
import next from 'eslint-config-next';
import tseslint from 'typescript-eslint'; // v7+
import prettier from 'eslint-config-prettier'; // Flat 지원

export default [
	{ ignores: ['node_modules/**', '.next/**'] },

	// 기본 JS 권장
	js.configs.recommended,

	// TypeScript 권장 (타입 미체크 버전: configs.recommended)
	// 타입체크 규칙까지 쓰려면 project 설정 추가 필요
	...tseslint.configs.recommended,

	// Next.js 권장
	...next(),

	// Prettier 충돌 해제
	prettier,

	// 프로젝트 커스텀
	{
		rules: {
			'@typescript-eslint/no-unused-vars': 'warn',
			'no-unused-vars': 'off',
			'import/order': [
				'warn',
				{
					groups: [
						['builtin', 'external'],
						'internal',
						['parent', 'sibling', 'index'],
					],
					pathGroups: [
						{
							pattern: '{react,react/**,next,next/**}',
							group: 'external',
							position: 'before',
						},
					],
					pathGroupsExcludedImportTypes: ['react', 'next'],
					'newlines-between': 'always',
					alphabetize: { order: 'asc', caseInsensitive: true },
				},
			],
		},
	},
];
