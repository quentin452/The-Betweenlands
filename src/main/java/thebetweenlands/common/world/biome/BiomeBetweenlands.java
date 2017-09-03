package thebetweenlands.common.world.biome;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.entity.mobs.EntityPyrad;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.EventSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.LocationSpawnEntry;
import thebetweenlands.common.world.biome.spawning.spawners.SurfaceSpawnEntry;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;
import thebetweenlands.common.world.storage.world.shared.location.EnumLocationType;
import thebetweenlands.util.IWeightProvider;

public class BiomeBetweenlands extends Biome implements IWeightProvider {
	protected final List<BLSpawnEntry> blSpawnEntries = new ArrayList<BLSpawnEntry>();
	private int grassColor, foliageColor;
	private short biomeWeight;
	private BiomeGenerator biomeGenerator;
	private int[] fogColorRGB = new int[]{(int) 255, (int) 255, (int) 255};

	public BiomeBetweenlands(BiomeProperties properties) {
		super(properties);
		this.spawnableCreatureList.clear();
		this.spawnableMonsterList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();
		this.biomeWeight = 100;
		this.topBlock = BlockRegistry.SWAMP_GRASS.getDefaultState();
		this.fillerBlock = BlockRegistry.SWAMP_DIRT.getDefaultState();
		this.biomeGenerator = new BiomeGenerator(this);

		this.setFogColor(10, 30, 22);
		this.addSpawnEntries();
	}

	/**
	 * Adds the entity spawn entries
	 */
	protected void addSpawnEntries() {
		this.blSpawnEntries.add(new EventSpawnEntry(new SurfaceSpawnEntry(EntityFirefly.class, (short) 280), "bloodSky").setSpawnCheckRadius(16.0D).setGroupSize(1, 4));
		this.blSpawnEntries.add(new EventSpawnEntry(new SurfaceSpawnEntry(EntitySwampHag.class, (short) 250), "bloodSky") {
			@Override
			protected EntityLiving createEntity(World world) {
				EntityLiving entity = super.createEntity(world);
				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32D);
				entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
				return entity;
			}
		}.setHostile(true));
		this.blSpawnEntries.add(new EventSpawnEntry(new SurfaceSpawnEntry(EntityPeatMummy.class, (short) 65), "bloodSky") {
			@Override
			protected EntityLiving createEntity(World world) {
				EntityLiving entity = super.createEntity(world);
				entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(EntityPeatMummy.BASE_SPEED + 0.075D);
				entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(EntityPeatMummy.BASE_DAMAGE + 2.0D);
				return entity;
			}
		}.setHostile(true).setSpawnCheckRadius(20.0D));

		this.blSpawnEntries.add(new LocationSpawnEntry(EntityPyrad.class, (short) 120, EnumLocationType.GIANT_TREE).setHostile(true).setSpawnCheckRadius(26.0D).setSpawningInterval(500));
	}

	/**
	 * Sets the biome generator
	 * @param generator
	 * @return
	 */
	protected final BiomeBetweenlands setBiomeGenerator(BiomeGenerator generator) {
		if(generator.getBiome() != this)
			throw new RuntimeException("Generator was assigned to a different biome!");
		this.biomeGenerator = generator;
		return this;
	}

	/**
	 * Returns the biome generator.
	 * If no generator was specified the default biome generator is returned
	 * @return
	 */
	public final BiomeGenerator getBiomeGenerator() {
		return this.biomeGenerator;
	}

	/**
	 * Returns the BL spawn entries
	 * @return
	 */
	public final List<BLSpawnEntry> getSpawnEntries() {
		return this.blSpawnEntries;
	}

	/**
	 * Sets Biome specific weighted probability.
	 * The default weight is 100.
	 * @param weight
	 */
	protected final BiomeBetweenlands setWeight(int weight) {
		this.biomeWeight = (short) weight;
		return this;
	}

	/**
	 * Sets the grass and foliage colors
	 * @param grassColor
	 * @param foliageColor
	 * @return
	 */
	public final BiomeBetweenlands setFoliageColors(int grassColor, int foliageColor) {
		this.grassColor = grassColor;
		this.foliageColor = foliageColor;
		return this;
	}

	/**
	 * Sets the biome fog color
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public final BiomeBetweenlands setFogColor(int red, int green, int blue) {
		this.fogColorRGB[0] = red;
		this.fogColorRGB[1] = green;
		this.fogColorRGB[2] = blue;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getGrassColorAtPos(BlockPos pos) {
		if(this.grassColor == 0)
			return super.getGrassColorAtPos(pos);
		return this.grassColor;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getFoliageColorAtPos(BlockPos pos) {
		if(this.foliageColor == 0)
			return super.getFoliageColorAtPos(pos);
		return this.foliageColor;
	}

	/**
	 * Returns Biome specific weighted probability.
	 */
	@Override
	public final short getWeight() {
		return this.biomeWeight;
	}

	/**
	 * Returns the distance where the fog starts to build up.
	 * @param farPlaneDistance Maximum render distance
	 * @return float
	 */
	@SideOnly(Side.CLIENT)
	public float getFogStart(float farPlaneDistance, int mode) {
		return mode == -1 ? 0.0F : farPlaneDistance * 0.5F;
	}

	/**
	 * Returns the distance where the fog is fully opaque.
	 * @param farPlaneDistance Maximum render distance
	 * @return float
	 */
	@SideOnly(Side.CLIENT)
	public float getFogEnd(float farPlaneDistance, int mode) {
		return farPlaneDistance;
	}

	/**
	 * Returns the fog RGB color.
	 * @return int[3]
	 */
	@SideOnly(Side.CLIENT)
	public int[] getFogRGB() {
		return this.fogColorRGB;
	}

	/**
	 * Called to update the fog range and color
	 */
	public void updateFog() {

	}

	public void addTypes(){

	}
}
