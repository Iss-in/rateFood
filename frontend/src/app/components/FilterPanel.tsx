import { Input } from "./ui/input";
import { Slider } from "./ui/slider";
import { Search, MapPin } from "lucide-react";

interface FilterPanelProps {
  onSearchChange: (search: string) => void;
  onRangeChange: (range: number) => void;
  searchValue: string;
  locationRange: number;
}

export function FilterPanel({
  onSearchChange,
  onRangeChange,
  searchValue,
  locationRange
}: FilterPanelProps) {

  return (
    <div className="bg-card p-6 rounded-lg shadow-sm border">
      <div className="space-y-6">
        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
          <Input
            placeholder="Search..."
            value={searchValue}
            onChange={(e) => onSearchChange(e.target.value)}
            className="pl-10"
          />
        </div>

        {/* Tags */}
        {/*<div>*/}
        {/*  <h3 className="mb-3">Tags</h3>*/}
        {/*  <div className="flex flex-wrap gap-2 mb-3">*/}
        {/*    {availableTags.map((tag) => (*/}
        {/*      <Badge*/}
        {/*        key={tag}*/}
        {/*        variant={selectedTags.includes(tag) ? "default" : "outline"}*/}
        {/*        className="cursor-pointer text-sm"*/}
        {/*        onClick={() => toggleTag(tag)}*/}
        {/*      >*/}
        {/*        {tag}*/}
        {/*      </Badge>*/}
        {/*    ))}*/}
        {/*  </div>*/}
        {/*  {selectedTags.length > 0 && (*/}
        {/*    <div className="flex flex-wrap gap-2">*/}
        {/*      {selectedTags.map((tag) => (*/}
        {/*          <Badge key={tag} variant="secondary" className="bg-gray-200 px-3 flex items-center space-x-1 text-sm">*/}
        {/*            {tag}*/}
        {/*          <button*/}
        {/*            className="p-0 m-0 text-sm leading-none cursor-pointer"*/}
        {/*            onClick={() => removeTag(tag)}*/}
        {/*          >×</button>*/}
        {/*        </Badge>*/}
        {/*      ))}*/}
        {/*    </div>*/}
        {/*  )}*/}
        {/*</div>*/}

        {/* Location Range */}
        {/* <div>
          <div className="flex items-center gap-2 mb-3">
            <MapPin className="h-4 w-4" />
            <h3>Distance: {locationRange} kilometers</h3>
          </div>
          <Slider
            value={[locationRange]}
            onValueChange={(value) => onRangeChange(value[0])}
            max={25}
            min={1}
            step={1}
            className="w-full"
          />
        </div> */}
      </div>
    </div>
  );
}