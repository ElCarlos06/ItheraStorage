export default function ProgressBar({
  step = 1,
  containerClassName = "progress",
}) {
  return (
    <div
      className={containerClassName}
      style={{ height: "7px", width: "200px", margin: "0 auto" }}
    >
      <div
        className={`progress-bar transition-all ${step === 1 ? "ithera-progress-bar-color" : ""}`}
        style={{
          width: "50%",
          backgroundColor: step === 1 ? undefined : "#818992ff",
        }}
      ></div>
      <div
        className={`progress-bar transition-all ${step === 2 ? "ithera-progress-bar-color" : ""}`}
        style={{
          width: "50%",
          backgroundColor: step === 2 ? undefined : "#818992ff",
        }}
      ></div>
    </div>
  );
}
