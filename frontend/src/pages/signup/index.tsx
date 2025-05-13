// src/pages/signup/index.tsx

import { useState } from 'react';

export default function SignUpPage() {
	const [name, setName] = useState('');
	const [email, setEmail] = useState('');
	const [password, setPassword] = useState('');

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		console.log('회원가입 정보:', { name, email, password });
		// TODO: 회원가입 API 연동
	};

	return (
		<div className="min-h-screen flex items-center justify-center bg-gray-100">
			<form
				onSubmit={handleSubmit}
				className="bg-white p-8 rounded shadow-md w-full max-w-md"
			>
				<h1 className="text-2xl font-semibold mb-6 text-center">회원가입</h1>

				<label className="block mb-2 text-sm font-medium">이름</label>
				<input
					type="text"
					value={name}
					onChange={(e) => setName(e.target.value)}
					required
					className="w-full px-4 py-2 mb-4 border rounded focus:outline-none focus:ring focus:border-blue-300"
				/>

				<label className="block mb-2 text-sm font-medium">이메일</label>
				<input
					type="email"
					value={email}
					onChange={(e) => setEmail(e.target.value)}
					required
					className="w-full px-4 py-2 mb-4 border rounded focus:outline-none focus:ring focus:border-blue-300"
				/>

				<label className="block mb-2 text-sm font-medium">비밀번호</label>
				<input
					type="password"
					value={password}
					onChange={(e) => setPassword(e.target.value)}
					required
					className="w-full px-4 py-2 mb-6 border rounded focus:outline-none focus:ring focus:border-blue-300"
				/>

				<button
					type="submit"
					className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 transition"
				>
					회원가입
				</button>
			</form>
		</div>
	);
}
