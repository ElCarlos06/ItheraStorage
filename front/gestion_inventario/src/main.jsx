import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { Toaster } from "sonner";
import { TooltipProvider } from "./components/Tooltip/Tooltip";

import "./styles/theme.css";
import "/node_modules/bootstrap/dist/css/bootstrap.min.css";
import "/node_modules/bootstrap-icons/font/bootstrap-icons.css";
import "/node_modules/bootstrap/dist/js/bootstrap.bundle.min.js";

import App from "./App.jsx";

createRoot(document.getElementById("root")).render(
  <BrowserRouter>
    <TooltipProvider>
      <App />
      <Toaster position="bottom-right" closeButton={false} />
    </TooltipProvider>
  </BrowserRouter>,
);
