namespace BSO
{
    public abstract class BSOTag
    {
        public abstract BSOType GetTagType();

        public virtual byte GetAdditionalData() { return 0; }

        public byte GetIDAD()
        {
            return (byte)(BSOUtils.GetTypeID(GetTagType()) + GetAdditionalData());
        }

        public abstract void Write(BinaryWriter bw);
    }
}