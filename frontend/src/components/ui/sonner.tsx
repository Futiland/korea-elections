import { AlertTriangle, CheckCircle } from 'lucide-react';
import { useTheme } from 'next-themes';
import { Toaster as Sonner, ToasterProps } from 'sonner';

const Toaster = ({ ...props }: ToasterProps) => {
	const { theme = 'system' } = useTheme();

	return (
		<Sonner
			theme={theme as ToasterProps['theme']}
			className="toaster group text-md"
			style={
				{
					'--normal-bg': 'rgba(0, 0, 0, 0.65)',
					'--normal-text': '#ffffff',
					'--normal-border': 'rgba(255, 255, 255, 0.12)',
					fontSize: '14px',
					borderRadius: '100px',
					color: '#ffffff',
				} as React.CSSProperties
			}
			{...props}
		/>
	);
};

export { Toaster };
