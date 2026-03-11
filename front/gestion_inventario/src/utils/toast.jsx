import { toast as sonnerToast } from "sonner";
import ToastWithProgress from "../components/ToastWithProgress/ToastWithProgress";

const DURATION = 3000;

export function toast(message, type = "success", duration = DURATION) {
  sonnerToast.custom(
    () => <ToastWithProgress message={message} type={type} duration={duration} />,
    { duration }
  );
}

toast.success = (message, duration = DURATION) => {
  toast(message, "success", duration);
};

toast.error = (message, duration = DURATION) => {
  toast(message, "error", duration);
};
