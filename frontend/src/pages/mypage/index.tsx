import { useState } from 'react';

export default function MyPage() {
	const [username] = useState('홍길동');
	const [email] = useState('hong@example.com');

	const handleLogout = () => {
		// TODO: 실제 로그아웃 처리 (예: 쿠키 제거, 토큰 삭제 등)
		console.log('로그아웃 처리');
		alert('로그아웃 되었습니다.');
	};

	return (
		<div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
			<div className="bg-white p-8 rounded shadow-md w-full max-w-md">
				<h1 className="text-2xl font-semibold mb-6 text-center">마이페이지</h1>

				<div className="mb-4">
					<p className="text-sm text-gray-600">이름</p>
					<p className="text-lg font-medium">{username}</p>
				</div>

				<div className="mb-6">
					<p className="text-sm text-gray-600">이메일</p>
					<p className="text-lg font-medium">{email}</p>
				</div>

				<button
					onClick={handleLogout}
					className="w-full bg-red-500 text-white py-2 rounded hover:bg-red-600 transition"
				>
					로그아웃
				</button>
			</div>
		</div>
	);
}
