import CreatePollView from './CreatePollView';
import {
	useCreatePollPresenter,
	type CreatePollDialogProps,
} from './useCreatePollPresenter';

export default function CreatePollDialog(props: CreatePollDialogProps) {
	const presenter = useCreatePollPresenter(props);

	return <CreatePollView {...presenter} />;
}
