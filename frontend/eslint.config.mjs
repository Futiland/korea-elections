import { dirname } from 'path';
import { fileURLToPath } from 'url';
import { FlatCompat } from '@eslint/eslintrc';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const compat = new FlatCompat({
	baseDirectory: __dirname,
});

const eslintConfig = [
	...compat.extends('next/core-web-vitals', 'next/typescript'),
	{
		extends: [
			'plugin:@typescript-eslint/recommended',
			'prettier',
			'prettier/prettier',
			'plugin:prettier/recommended',
			'next/core-web-vitals',
		],
		rules: {
			'@typescript-eslint/no-unused-vars': 'warn',
			'no-unused-vars': 'warn',
			'@typescript-eslint/no-empty-interface': 'warn',
			'@typescript-eslint/no-unused-expressions': 'warn',
			'@typescript-eslint/no-explicit-any': 'warn',
			'@typescript-eslint/no-empty-object-type': 'warn',
			'@typescript-eslint/no-unused-vars': 'warn',
			'no-unused-vars': 'warn',
			'@typescript-eslint/no-non-null-assertion': 'warn',
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
					alphabetize: {
						order: 'asc',
						caseInsensitive: true,
					},
				},
			],
		},
	},
];

export default eslintConfig;
