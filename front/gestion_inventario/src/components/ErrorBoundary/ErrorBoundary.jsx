import { Component } from "react";
import { AlertTriangle } from "lucide-react";
import Button from "../Button/Button";
import "./ErrorBoundary.css";

/**
 * Captura errores de React y muestra una vista amigable en lugar del fallo.
 */
export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error("Error capturado:", error, errorInfo);
  }

  handleRetry = () => {
    this.setState({ hasError: false, error: null });
  };

  handleGoHome = () => {
    this.setState({ hasError: false, error: null });
    window.location.href = "/";
  };

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-boundary">
          <div className="error-boundary__card">
            <div className="error-boundary__icon">
              <AlertTriangle size={48} strokeWidth={2} />
            </div>
            <h1 className="error-boundary__title">Algo salió mal</h1>
            <p className="error-boundary__message">
              Ocurrió un error inesperado. Intenta recargar la página o volver al inicio.
            </p>
            <div className="error-boundary__actions">
              <Button variant="outline" onClick={this.handleRetry}>
                Intentar de nuevo
              </Button>
              <Button variant="primary" onClick={this.handleGoHome}>
                Ir al inicio
              </Button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
