export default function LogoHeader({ containerClassName = "text-end mb-4" }) {
  return (
    <div className={containerClassName}>
      <div className="d-flex align-items-center justify-content-center">
        <img
          src="/src/assets/activos360_logo.png"
          alt="Logo Ithera"
          className="img-fluid"
        />
      </div>
    </div>
  );
}
