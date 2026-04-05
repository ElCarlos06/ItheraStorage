import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Toaster } from "sonner";
import { TooltipProvider } from "./components/Tooltip/Tooltip";

import "./styles/theme.css";
import "/node_modules/bootstrap/dist/css/bootstrap.min.css";
import "/node_modules/bootstrap-icons/font/bootstrap-icons.css";

import App from "./App.jsx";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5,        // 5 min → no refetch automático mientras sea "fresh"
      refetchOnWindowFocus: false,      // no recargar al volver a la pestaña
      retry: 1,                         // solo 1 reintento en caso de error
    },
  },
});

createRoot(document.getElementById("root")).render(
  <BrowserRouter>
    <QueryClientProvider client={queryClient}>
      <TooltipProvider>
        <App />
        <Toaster position="bottom-right" closeButton={false} />
      </TooltipProvider>
    </QueryClientProvider>
  </BrowserRouter>,
);
