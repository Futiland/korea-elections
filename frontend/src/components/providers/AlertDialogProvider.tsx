// import { Dialog, DialogContent } from '@/components/ui/dialog';
import {
	createContext,
	useCallback,
	useContext,
	useState,
	ReactNode,
} from 'react';
import { Dialog, DialogContent } from '@/components/CustomDialog';
import { cn } from '@/lib/utils';

interface AlertDialogContextType {
	showDialog: (options: { message: string; actions: ReactNode }) => void;
	hideDialog: () => void;
}

const AlertDialogContext = createContext<AlertDialogContextType | undefined>(
	undefined
);

export const AlertDialogProvider = ({ children }: { children: ReactNode }) => {
	const [open, setOpen] = useState(false);
	const [message, setMessage] = useState('');
	const [actions, setActions] = useState<ReactNode>(null);

	const showDialog = useCallback(
		(options: { message: string; actions: ReactNode }) => {
			setMessage(options.message);
			setActions(options.actions);
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
						'w-[calc(100%-40px)] max-w-sm bg-white rounded-md shadow-xl p-6 transition-all duration-300 data-[state=open]:opacity-100 data-[state=open]:scale-100 data-[state=closed]:opacity-0 data-[state=closed]:scale-95 overlay:bg-black/40 overlay:backdrop-blur-sm overlay:transition-all overlay:duration-300 overlay:data-[state=open]:opacity-100 overlay:data-[state=closed]:opacity-0'
					}
				>
					<div className="space-y-4">
						<p className="text-base font-medium">{message}</p>
						<div className="flex flex-col gap-2">{actions}</div>
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
