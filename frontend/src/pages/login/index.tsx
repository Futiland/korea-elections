import { useState } from 'react';

export default function LoginPage() {
	const [idNum, setIdNum] = useState('');
	const [password, setPassword] = useState('');

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		console.log('로그인 시도:', { idNum, password });
		// TODO: 로그인 API 호출
	};

	return (
		<div className="min-h-screen flex items-center justify-center bg-gray-100">
			<form
				onSubmit={handleSubmit}
				className="bg-white p-8 rounded shadow-md w-full max-w-sm"
			>
				<h1 className="text-2xl font-semibold mb-6 text-center">로그인</h1>

				<label className="block mb-2 text-sm font-medium">휴대폰 번호</label>
				<input
					type="idNum"
					value={idNum}
					onChange={(e) => setIdNum(e.target.value)}
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
					className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
				>
					로그인
				</button>
			</form>
		</div>
	);
}
