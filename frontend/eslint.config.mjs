import js from '@eslint/js';
import tseslint from 'typescript-eslint';
import prettier from 'eslint-config-prettier';
import importPlugin from 'eslint-plugin-import';
import nextPlugin from '@next/eslint-plugin-next';

export default [
	{ ignores: ['.next/**', 'node_modules/**'] },

	js.configs.recommended,
	...tseslint.configs.recommended,
	prettier,

	{
		plugins: { import: importPlugin, '@next/next': nextPlugin },

		// 필요하면 Next 권장 규칙 유지
		// rules: { ...nextPlugin.configs['core-web-vitals'].rules, ... },

		rules: {
			// ✅ 경고 끄기(원천 제거)
			'import/order': 'off',
			'@typescript-eslint/no-unused-vars': 'off',
			'no-unused-vars': 'off',
			'@typescript-eslint/no-explicit-any': 'off',
			'@typescript-eslint/no-unused-expressions': 'off',
			'@typescript-eslint/no-empty-object-type': 'off',
			'no-useless-escape': 'off',
			'prefer-const': 'off',
			'no-useless-escape': 'off',
		},
	},
];
