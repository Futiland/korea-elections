import {
	createContext,
	useCallback,
	useContext,
	useState,
	ReactNode,
} from 'react';
import { Dialog, DialogContent } from '@/components/CustomDialog';

interface AlertDialogContextType {
	showDialog: (options: {
		message: string;
		actions: ReactNode;
		discription?: string;
	}) => void;
	hideDialog: () => void;
}

const AlertDialogContext = createContext<AlertDialogContextType | undefined>(
	undefined
);

export const AlertDialogProvider = ({ children }: { children: ReactNode }) => {
	const [open, setOpen] = useState(false);
	const [message, setMessage] = useState('');
	const [actions, setActions] = useState<ReactNode>(null);
	const [discription, setDiscription] = useState('');

	const showDialog = useCallback(
		(options: {
			message: string;
			actions: ReactNode;
			discription?: string;
		}) => {
			setMessage(options.message);
			setActions(options.actions);
			setDiscription(options.discription || '');
			setOpen(true);
		},
		[]
	);

	const hideDialog = useCallback(() => {
		setOpen(false);
	}, []);

	return (
		<AlertDialogContext.Provider value={{ showDialog, hideDialog }}>
			{children}
			<Dialog open={open} onOpenChange={setOpen}>
				<DialogContent
					className={
						'w-[calc(100%-40px)] max-w-sm bg-white rounded-md shadow-xl p-6 transition-all duration-300 data-[state=open]:opacity-100 data-[state=open]:scale-100 data-[state=closed]:opacity-0 data-[state=closed]:scale-95'
					}
				>
					<div className="space-y-2 text-center">
						<p className="text-lg font-semibold">{message}</p>
						<p className="text-sm text-muted-foreground text-left">
							{discription}
						</p>
						<div className="flex flex-col gap-2 mt-6">{actions}</div>
					</div>
				</DialogContent>
			</Dialog>
		</AlertDialogContext.Provider>
	);
};

export const useAlertDialog = () => {
	const context = useContext(AlertDialogContext);
	if (!context)
		throw new Error('useAlertDialog must be used within AlertDialogProvider');
	return context;
};
